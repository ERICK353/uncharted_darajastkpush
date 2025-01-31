package network

import models.StkPushRequest
import models.StkPushResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface DarajaApiService {
    @POST("mpesa/stkpush/v1/processrequest")
    fun initiateStkPush(
        @Header("Authorization") authHeader: String,
        @Body request: StkPushRequest
    ): Call<StkPushResponse>
}