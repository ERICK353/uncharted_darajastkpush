package com.example.darajastkpush.network

import com.example.darajastkpush.models.AccessTokenResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface DarajaOAuthService {
    @FormUrlEncoded
    @POST("oauth/v1/generate")
    fun getAccessToken(
        @Header("Authorization") authHeader: String,
        @Field("grant_type") grantType: String = "client_credentials"
    ): Call<AccessTokenResponse>
}
