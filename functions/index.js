const functions = require("firebase-functions");
const admin = require("firebase-admin");
const axios = require("axios");

admin.initializeApp();

const consumerKey = "YOUR_CONSUMER_KEY";
const consumerSecret = "YOUR_CONSUMER_SECRET";
const shortCode = "174379";
const passKey = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919";
const baseUrl = "https://sandbox.safaricom.co.ke";

async function getAccessToken() {
  const auth = Buffer.from(`${consumerKey}:${consumerSecret}`).toString("base64");
  const response = await axios.get(`${baseUrl}/oauth/v1/generate?grant_type=client_credentials`, {
    headers: { Authorization: `Basic ${auth}` },
  });
  return response.data.access_token;
}

exports.initiateMpesaPayment = functions.https.onCall(async (data, context) => {
  if (!context.auth) {
    throw new functions.https.HttpsError("unauthenticated", "User must be authenticated.");
  }

  const { phoneNumber, amount, productId, userId } = data;
  if (!phoneNumber || !amount || !productId || !userId) {
    throw new functions.https.HttpsError("invalid-argument", "Missing required parameters.");
  }

  try {
    const token = await getAccessToken();
    const timestamp = new Date().toISOString().replace(/[^0-9]/g, "").slice(0, 14);
    const password = Buffer.from(`${shortCode}${passKey}${timestamp}`).toString("base64");

    const paymentData = {
      BusinessShortCode: shortCode,
      Password: password,
      Timestamp: timestamp,
      TransactionType: "CustomerPayBillOnline",
      Amount: amount,
      PartyA: phoneNumber,
      PartyB: shortCode,
      PhoneNumber: phoneNumber,
      CallBackURL: `https://us-central1-YOUR_FIREBASE_PROJECT_ID.cloudfunctions.net/mpesaCallback`,
      AccountReference: "KylesEmporium",
      TransactionDesc: "Product Purchase",
    };

    const paymentRef = admin.firestore().collection("payments").doc();
    await paymentRef.set({
      productId,
      userId,
      phoneNumber,
      amount,
      status: "Pending",
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
    });

    const response = await axios.post(
      `${baseUrl}/mpesa/stkpush/v1/processrequest`,
      paymentData,
      { headers: { Authorization: `Bearer ${token}` } }
    );

    await paymentRef.update({
      merchantRequestID: response.data.MerchantRequestID,
      checkoutRequestID: response.data.CheckoutRequestID,
    });

    return { message: "STK Push initiated", paymentId: paymentRef.id };
  } catch (error) {
    console.error("Error initiating payment:", error);
    throw new functions.https.HttpsError("internal", error.message);
  }
});

exports.mpesaCallback = functions.https.onRequest(async (req, res) => {
  const callbackData = req.body;
  console.log("M-Pesa Callback:", JSON.stringify(callbackData, null, 2));

  if (callbackData.Body && callbackData.Body.stkCallback) {
    const { MerchantRequestID, CheckoutRequestID, ResultCode, ResultDesc } = callbackData.Body.stkCallback;

    const paymentQuery = admin.firestore().collection("payments")
      .where("merchantRequestID", "==", MerchantRequestID)
      .where("checkoutRequestID", "==", CheckoutRequestID);

    const snapshot = await paymentQuery.get();
    if (!snapshot.empty) {
      const paymentDoc = snapshot.docs[0];
      const status = ResultCode === 0 ? "Success" : "Failed";
      const updateData = {
        status,
        updatedAt: admin.firestore.FieldValue.serverTimestamp(),
      };
      if (ResultCode !== 0) {
        updateData.error = ResultDesc;
      }
      await paymentDoc.ref.update(updateData);
    }
  }

  res.json({ message: "Callback received" });
});