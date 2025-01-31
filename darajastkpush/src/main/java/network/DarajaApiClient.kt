package network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DarajaApiClient {
    private const val BASE_URL = "https://sandbox.safaricom.co.ke/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val oAuthService: DarajaOAuthService by lazy {
        retrofit.create(DarajaOAuthService::class.java)
    }

    val stkPushService: DarajaStkPushService by lazy {
        retrofit.create(DarajaStkPushService::class.java)
    }
}