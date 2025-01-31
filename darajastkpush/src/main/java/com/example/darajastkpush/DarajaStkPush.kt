package com.example.darajastkpush

import models.StkPushRequest
import models.StkPushResponse
import network.DarajaApiClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback

class DarajaStkPush(private val apiKey: String, private val apiSecret: String) {

    fun initiateStkPush(
        phoneNumber: String,
        amount: Double,
        accountReference: String,
        transactionDesc: String,
        callback: (Boolean, String?) -> Unit
    ) {
        // Create the request
        val request = StkPushRequest(phoneNumber, amount, accountReference, transactionDesc)

        // Generate the auth header (basic auth for simplicity)
        val authHeader = "Bearer ${generateAuthToken()}"

        // Make the API call
        val call = DarajaApiClient.instance.initiateStkPush(authHeader, request)
        call.enqueue(object:Callback<StkPushResponse> {
            override fun onResponse(call: Call<StkPushResponse>, response: Response<StkPushResponse>) {
                if (response.isSuccessful) {
                    val stkPushResponse = response.body()
                    if (stkPushResponse?.success == true) {
                        callback(true, stkPushResponse.message)
                    } else {
                        callback(false, stkPushResponse?.message ?: "Unknown error")
                    }
                } else {
                    callback(false, "Failed to initiate STK push: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<StkPushResponse>, t: Throwable) {
                callback(false, "Network error: ${t.message}")
            }
        })
    }

    private fun generateAuthToken(): String {
        // Implement your logic to generate an auth token (e.g., using apiKey and apiSecret)
        return "$apiKey:$apiSecret".encodeToBase64()
    }

    private fun String.encodeToBase64(): String {
        return android.util.Base64.encodeToString(this.toByteArray(), android.util.Base64.NO_WRAP)
    }
}