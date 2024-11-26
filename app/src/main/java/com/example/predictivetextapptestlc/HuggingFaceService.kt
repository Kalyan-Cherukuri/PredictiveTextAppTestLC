package com.example.predictivetextapptestlc

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

// Request body for Hugging Face API
data class HuggingFaceRequest(
    val inputs: String, // User input text
    val parameters: Map<String, Any> = mapOf(
        "max_length" to 1,      // Limit to 1 token (one word)
        "temperature" to 0,   // Low temperature for deterministic output
        "return_full_text" to false // Generate only the continuation
    )
)


// Response body for Hugging Face API
data class HuggingFaceResponse(
    val generated_text: String // Predicted text from the API
)

// Retrofit interface for the Hugging Face API
interface HuggingFaceService {
    @Headers(
        "Authorization: Bearer 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", // Replace YOUR_API_KEY with your actual Hugging Face token
        "Content-Type: application/json"
    )
    @POST("https://api-inference.huggingface.co/models/gpt2") // Replace "gpt2" with your desired model (e.g., "tiiuae/falcon-7b")
    suspend fun getSuggestions(@Body request: HuggingFaceRequest): List<HuggingFaceResponse>
}
