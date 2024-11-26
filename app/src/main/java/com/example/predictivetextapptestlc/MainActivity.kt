package com.example.predictivetextapptestlc

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.* // Import layout components
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
suspend fun fetchNextWord(
    inputText: String,
    suggestions: MutableState<List<String>>
) {
    if (inputText.isBlank()) {
        suggestions.value = emptyList()
        return
    }

    val service = HuggingFaceRetrofitInstance.api
    val request = HuggingFaceRequest(
        inputs = inputText,
        parameters = mapOf(
            "max_length" to 1,       // Limit to 1 token (one word)
            "temperature" to 0.1,    // Low temperature for determinism
            "return_full_text" to false // Focus only on the continuation
        )
    )

    try {
        val response = withContext(Dispatchers.IO) {
            service.getSuggestions(request)
        }

        // Log raw response for debugging
        Log.d("HuggingFaceResponse", "Raw Response: $response")

        // Extract the first word from the response
        val nextWord = response.firstOrNull()?.generated_text?.trim()?.split(" ")?.firstOrNull() ?: ""

        // Update suggestions with the single predicted word
        suggestions.value = if (nextWord.isNotBlank()) listOf(nextWord) else emptyList()

    } catch (e: retrofit2.HttpException) {
        val errorBody = e.response()?.errorBody()?.string()
        Log.e("fetchNextWord", "HTTP Error: ${e.code()}, Body: $errorBody")
        suggestions.value = listOf("HTTP Error: ${e.code()} - $errorBody")
    } catch (e: Exception) {
        Log.e("fetchNextWord", "General Error: ${e.message}")
        e.printStackTrace()
        suggestions.value = listOf("Error fetching next word")
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

                // Fetch next word dynamically
                coroutineScope.launch {
                    fetchNextWord(newText, suggestions)
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
                            // Append the predicted word to the text field
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

