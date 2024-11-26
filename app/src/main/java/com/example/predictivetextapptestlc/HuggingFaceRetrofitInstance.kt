package com.example.predictivetextapptestlc

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object HuggingFaceRetrofitInstance {
    val api: HuggingFaceService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api-inference.huggingface.co/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HuggingFaceService::class.java)
    }
}
