package com.example.predictivetextapptestlc

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.predictivetextapptestlc.ui.theme.PredictiveTextAppTestLCTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PredictiveTextAppTestLCTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    PredictiveTextAppScreen()
                }
            }
        }
    }
}

// Function to fetch suggestions from Hugging Face API
suspend fun fetchHuggingFaceNextWords(
    inputText: String,
    suggestions: MutableState<List<String>>
) {
    if (inputText.isBlank()) {
        suggestions.value = emptyList()
        return
    }

    val service = HuggingFaceRetrofitInstance.api
    // Prompt to generate the next 2-3 words
    val prompt = "The next 2-3 words after \"$inputText\" are:"
    val request = HuggingFaceRequest(
        inputs = prompt,
        parameters = mapOf(
            "max_length" to 15,         // Allow space for 2-3 words
            "temperature" to 0.5,      // Balance creativity and determinism
            "num_return_sequences" to 1 // Single response
        )
    )

    try {
        val response = withContext(Dispatchers.IO) {
            service.getSuggestions(request)
        }
        // Log the raw response for debugging
        Log.d("HuggingFaceResponse", "Raw Response: $response")

        // Extract and process the next 2-3 words
        val rawText = response.firstOrNull()?.generated_text?.trim() ?: ""
        val nextWords = rawText
            .replace(prompt, "")  // Remove the prompt from the response
            .trim()               // Clean up whitespace
            .split(" ")           // Split the generated text into words
            .take(3)              // Limit to 2-3 words
            .joinToString(" ")    // Rejoin as a single string

        // Update suggestions with the predicted words
        suggestions.value = if (nextWords.isNotBlank()) listOf(nextWords) else emptyList()

    } catch (e: retrofit2.HttpException) {
        val errorBody = e.response()?.errorBody()?.string()
        Log.e("fetchHuggingFaceNextWords", "HTTP Error: ${e.code()}, Body: $errorBody")
        suggestions.value = listOf("HTTP Error: ${e.code()} - $errorBody")
    } catch (e: Exception) {
        Log.e("fetchHuggingFaceNextWords", "General Error: ${e.message}")
        e.printStackTrace()
        suggestions.value = listOf("Error fetching next words")
    }
}



@Composable
fun PredictiveTextAppScreen() {
    val textState = remember { mutableStateOf("") }
    val suggestions = remember { mutableStateOf(listOf<String>()) }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = textState.value,
            onValueChange = { newText ->
                textState.value = newText

                // Fetch next words dynamically for the input text
                coroutineScope.launch {
                    fetchHuggingFaceNextWords(newText, suggestions)
                }
            },
            label = { Text("Type something...") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(suggestions.value) { suggestion ->
                Text(
                    text = suggestion,
                    modifier = Modifier
                        .padding(4.dp)
                        .background(Color.LightGray)
                        .clickable {
                            // Append the predicted words to the text field
                            textState.value = if (textState.value.isBlank()) {
                                suggestion
                            } else {
                                "${textState.value} $suggestion"
                            }
                        }
                        .padding(8.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
