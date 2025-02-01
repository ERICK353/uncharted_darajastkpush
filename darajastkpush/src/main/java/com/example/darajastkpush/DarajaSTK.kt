package com.example.darajastkpush

import android.content.Context
import android.util.Base64
import com.example.darajastkpush.models.STKPushRequest
import com.example.darajastkpush.models.STKPushResponse


import com.example.darajastkpush.network.RetrofitClient

class DarajaSTK(
    private val context: Context,
    private val consumerKey: String,
    private val consumerSecret: String
) {

    private var accessToken: String? = null
    private var tokenExpiryTime: Long = 0

    suspend fun initiateSTKPush(
        businessShortCode: String,
        password: String,
        timestamp: String,
        transactionType: String,
        amount: String,
        partyA: String,
        partyB: String,
        phoneNumber: String,
        callBackURL: String,
        accountReference: String,
        transactionDesc: String
    ): Result<STKPushResponse> {
        return try {
            // Fetch the access token
            val accessTokenResult = getAccessToken()
            if (accessTokenResult is Result.Error) {
                return accessTokenResult
            }

            val accessToken = (accessTokenResult as Result.Success).data

            // Create the STK push request
            val request = STKPushRequest(
                BusinessShortCode = businessShortCode,
                Password = password,
                Timestamp = timestamp,
                TransactionType = transactionType,
                Amount = amount,
                PartyA = partyA,
                PartyB = partyB,
                PhoneNumber = phoneNumber,
                CallBackURL = callBackURL,
                AccountReference = accountReference,
                TransactionDesc = transactionDesc
            )

            // Make the STK push request
            val response = RetrofitClient.instance.initiateSTKPush("Bearer $accessToken", request)
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("STK Push Failed: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Result.Error("Network Error: ${e.message}")
        }
    }

    private suspend fun getAccessToken(): Result<String> {
        if (accessToken != null && System.currentTimeMillis() < tokenExpiryTime) {
            return Result.Success(accessToken!!)
        }

        return try {
            val authHeader = getAuthHeader(consumerKey, consumerSecret)
            val response = RetrofitClient.instance.getAccessToken(authHeader)
            if (response.isSuccessful) {
                accessToken = response.body()!!.access_token
                tokenExpiryTime = System.currentTimeMillis() + (response.body()!!.expires_in.toLong() * 1000)
                Result.Success(accessToken!!)
            } else {
                Result.Error("Failed to fetch access token: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Result.Error("Network Error: ${e.message}")
        }
    }

    private fun getAuthHeader(consumerKey: String, consumerSecret: String): String {
        val credentials = "$consumerKey:$consumerSecret"
        return "Basic ${Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)}"
    }

    sealed class Result<out T> {
        data class Success<out T>(val data: T) : Result<T>()
        data class Error(val message: String) : Result<Nothing>()
    }
}