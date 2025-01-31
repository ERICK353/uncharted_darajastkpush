package network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DarajaApiClient {
    private const val BASE_URL = "https://sandbox.safaricom.co.ke/"

    val instance: DarajaApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(DarajaApiService::class.java)
    }
}