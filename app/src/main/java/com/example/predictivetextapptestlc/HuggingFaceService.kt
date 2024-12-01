package com.example.predictivetextapptestlc

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

// Request body for Hugging Face API
data class HuggingFaceRequest(
    val inputs: String, // User input text
    val parameters: Map<String, Any> = mapOf(
        "max_length" to 10, // Number of tokens to generate
        "temperature" to 0.5 // Randomness level
    )
)

// Response body from Hugging Face API
data class HuggingFaceResponse(val generated_text: String)

// Retrofit interface for Hugging Face API
interface HuggingFaceService {
    @Headers(
        "Authorization: Bearer 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", // Replace with your Hugging Face API key
        "Content-Type: application/json"
    )
    @POST("https://api-inference.huggingface.co/models/gpt2") // Use a Hugging Face model endpoint
    suspend fun getSuggestions(@Body request: HuggingFaceRequest): List<HuggingFaceResponse>
}
