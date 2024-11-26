package com.example.predictivetextapptestlc

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object HuggingFaceRetrofitInstance {
    private const val BASE_URL = "https://api-inference.huggingface.co/"

    // Define the OkHttpClient for timeout and logging
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .callTimeout(30, TimeUnit.SECONDS) // Set call timeout to 30 seconds
            .connectTimeout(10, TimeUnit.SECONDS) // Set connection timeout
            .readTimeout(30, TimeUnit.SECONDS) // Set read timeout
            .writeTimeout(30, TimeUnit.SECONDS) // Set write timeout
            .build()
    }

    // Retrofit instance
    val api: HuggingFaceService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Attach OkHttpClient
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HuggingFaceService::class.java)
    }
}
