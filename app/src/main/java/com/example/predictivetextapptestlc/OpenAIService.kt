package com.example.predictivetextapptestlc

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

// OpenAI Request Data Class
data class OpenAIRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<Map<String, String>>,
    val max_tokens: Int = 5,
    val temperature: Double = 0.5
)

// OpenAI Response Data Classes
data class OpenAIResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)

data class Message(
    val role: String,
    val content: String
)

// Retrofit Interface
interface OpenAIService {
    @Headers(
        "Authorization: Bearer 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", // Replace YOUR_API_KEY with your actual API key
        "Content-Type: application/json"
    )
    @POST("https://api.openai.com/v1/chat/completions")
    suspend fun generateNextWord(@Body request: OpenAIRequest): OpenAIResponse
}
