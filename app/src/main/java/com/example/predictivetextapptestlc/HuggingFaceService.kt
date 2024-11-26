package com.example.predictivetextapptestlc

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

// Request body for Hugging Face API
data class HuggingFaceRequest(
    val inputs: String, // User input text
    val parameters: Map<String, Any> = mapOf(
        "max_length" to 20,      // Maximum length of the completion
        "temperature" to 0.7    // Creativity of the generated response
    )
)

// Response body from Hugging Face API
data class HuggingFaceResponse(val generated_text: String)

// Retrofit interface for the Hugging Face API
interface HuggingFaceService {
    @Headers(
        "Authorization: Bearer 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", // Replace YOUR_API_KEY with your actual token
        "Content-Type: application/json"
    )
    @POST("https://api-inference.huggingface.co/models/gpt2")
    suspend fun getSuggestions(@Body request: HuggingFaceRequest): List<HuggingFaceResponse>
}