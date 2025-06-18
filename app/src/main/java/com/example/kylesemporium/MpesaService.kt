package com.kylesemporium.app.utils

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface MpesaService {
    @GET
    suspend fun getAccessToken(
        @Url url: String,
        @Query("grant_type") grantType: String = "client_credentials"
    ): AccessTokenResponse

    @POST
    suspend fun initiateStkPush(
        @Url url: String,
        @Body request: StkPushRequest
    ): StkPushResponse
}

data class AccessTokenResponse(
    val access_token: String,
    val expires_in: String
)

data class StkPushRequest(
    val BusinessShortCode: String,
    val Password: String,
    val Timestamp: String,
    val TransactionType: String,
    val Amount: String,
    val PartyA: String,
    val PartyB: String,
    val PhoneNumber: String,
    val CallBackURL: String,
    val AccountReference: String,
    val TransactionDesc: String
)

data class StkPushResponse(
    val MerchantRequestID: String,
    val CheckoutRequestID: String,
    val ResponseCode: String,
    val ResponseDescription: String,
    val CustomerMessage: String
)