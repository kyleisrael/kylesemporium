package com.example.kylesemporium.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.kylesemporium.app.data.Product
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun PaymentScreen(navController: NavController, productId: String) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var phoneNumber by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var product by remember { mutableStateOf<Product?>(null) }
    var paymentStatus by remember { mutableStateOf<String?>(null) }
    val db = Firebase.firestore
    val functions = Firebase.functions

    LaunchedEffect(productId) {
        try {
            val doc = db.collection("products").document(productId).get().await()
            product = doc.toObject(Product::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to load product: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Listen for payment status updates
    LaunchedEffect(productId) {
        product?.let { prod ->
            db.collection("payments")
                .whereEqualTo("productId", productId)
                .whereEqualTo("userId", Firebase.auth.currentUser?.uid)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        errorMessage = "Error listening for payment status: ${e.message}"
                        return@addSnapshotListener
                    }
                    snapshot?.documents?.firstOrNull()?.let { doc ->
                        paymentStatus = doc.getString("status")
                        if (paymentStatus == "Success") {
                            Toast.makeText(context, "Payment successful!", Toast.LENGTH_LONG).show()
                            navController.navigate("home")
                        } else if (paymentStatus == "Failed") {
                            errorMessage = "Payment failed: ${doc.getString("error")}"
                            isLoading = false
                        }
                    }
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        product?.let {
            Text(
                text = "Purchase ${it.name}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "$${String.format("%.2f", it.price)}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        } ?: Text("Loading product...", style = MaterialTheme.typography.bodyLarge)

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number (e.g., +2547XXXXXXXX)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            enabled = !isLoading
        )

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = {
                product?.let { prod ->
                    if (phoneNumber.isNotEmpty()) {
                        val formattedPhone = phoneNumber.let {
                            if (it.startsWith("0")) "254${it.substring(1)}"
                            else if (it.startsWith("+254")) it.substring(1)
                            else it
                        }
                        if (formattedPhone.length == 12 && formattedPhone.startsWith("254")) {
                            isLoading = true
                            scope.launch {
                                try {
                                    val data = hashMapOf(
                                        "phoneNumber" to formattedPhone,
                                        "amount" to prod.price.toInt().toString(),
                                        "productId" to productId,
                                        "userId" to (Firebase.auth.currentUser?.uid ?: "")
                                    )
                                    val result = functions
                                        .getHttpsCallable("initiateMpesaPayment")
                                        .call(data)
                                        .await()
                                    Toast.makeText(context, "STK Push initiated", Toast.LENGTH_LONG).show()
                                } catch (e: Exception) {
                                    errorMessage = "Payment initiation failed: ${e.message}"
                                    isLoading = false
                                }
                            }
                        } else {
                            errorMessage = "Invalid phone number format"
                        }
                    } else {
                        errorMessage = "Please enter a phone number"
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            enabled = !isLoading && product != null
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
            } else {
                Text("Pay with M-Pesa", color = MaterialTheme.colorScheme.onPrimary, fontSize = 18.sp)
            }
        }

        TextButton(
            onClick = { navController.navigate("home") },
            modifier = Modifier.padding(top = 16.dp),
            enabled = !isLoading
        ) {
            Text("Back to Home", color = MaterialTheme.colorScheme.primary)
        }
    }
}