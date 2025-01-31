package com.example.darajastkpush

import models.OAuthTokenResponse
import models.StkPushRequest
import models.StkPushResponse
import network.DarajaApiClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback

import okhttp3.Credentials


class DarajaStkPush(private val consumerKey: String, private val consumerSecret: String) {

    private var accessToken: String? = null
    private var tokenExpiryTime: Long = 0

    fun initiateStkPush(
        phoneNumber: String,
        amount: Double,
        accountReference: String,
        transactionDesc: String,
        callback: (Boolean, String?) -> Unit
    ) {
        // Get the access token
        getAccessToken { token ->
            if (token == null) {
                callback(false, "Failed to generate access token")
                return@getAccessToken
            }

            // Create the request
            val request = StkPushRequest(phoneNumber, amount, accountReference, transactionDesc)

            // Make the API call
            val call = DarajaApiClient.stkPushService.initiateStkPush("Bearer $token", request)
            call.enqueue(object : Callback<StkPushResponse> {
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
    }

    private fun getAccessToken(callback: (String?) -> Unit) {
        if (accessToken != null && System.currentTimeMillis() < tokenExpiryTime) {
            callback(accessToken)
            return
        }

        // Generate Basic Auth credentials
        val credentials = Credentials.basic(consumerKey, consumerSecret)

        // Make the OAuth token request
        val call = DarajaApiClient.oAuthService.getAccessToken(credentials)
        call.enqueue(object : Callback<OAuthTokenResponse> {
            override fun onResponse(call: Call<OAuthTokenResponse>, response: Response<OAuthTokenResponse>) {
                if (response.isSuccessful) {
                    val tokenResponse = response.body()
                    if (tokenResponse != null) {
                        accessToken = tokenResponse.accessToken
                        tokenExpiryTime = System.currentTimeMillis() + 3600 * 1000 // 1 hour
                        callback(accessToken)
                    } else {
                        callback(null)
                    }
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<OAuthTokenResponse>, t: Throwable) {
                callback(null)
            }
        })
    }
}