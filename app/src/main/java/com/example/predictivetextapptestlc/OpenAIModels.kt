/*// OpenAIService.kt

package com.example.predictivetextapptestlc // Ensure this matches your project package

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

// Define data classes
data class ChatRequest(val model: String, val prompt: String, val max_tokens: Int)
data class ChatResponse(val choices: List<Choice>)
data class Choice(val text: String)

// Retrofit interface for the OpenAI API
interface OpenAIService {
    @Headers("Content-Type: application/json", "Authorization: Bearer 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")
    @POST("v1/completions")
    suspend fun getSuggestions(@Body request: ChatRequest): ChatResponse
}

// Singleton object for Retrofit instance
object RetrofitInstance {
    private const val BASE_URL = "https://api.openai.com/"

    val api: OpenAIService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenAIService::class.java)
    }
}
*/