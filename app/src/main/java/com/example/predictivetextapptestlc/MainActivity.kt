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

// Fetch Suggestions from Local Dictionary
fun getDictionarySuggestions(input: String, dictionary: List<String>): List<String> {
    return if (input.isBlank()) {
        emptyList()
    } else {
        dictionary.filter { it.startsWith(input, ignoreCase = true) }.take(5)
    }
}

// Fetch Hybrid Suggestions (Dictionary + Hugging Face)
suspend fun fetchHybridSuggestions(
    inputText: String,
    suggestions: MutableState<List<String>>,
    dictionary: List<String>
) {
    if (inputText.isBlank()) {
        suggestions.value = emptyList()
        return
    }

    // Fetch suggestions from the local dictionary
    val localSuggestions = getDictionarySuggestions(inputText, dictionary)

    try {
        // Fetch suggestions from Hugging Face API
        val service = HuggingFaceRetrofitInstance.api
        val request = HuggingFaceRequest(
            inputs = inputText,
            parameters = mapOf(
                "max_length" to 5, // Number of tokens to generate
                "temperature" to 0.5 // Randomness
            )
        )

        val response = withContext(Dispatchers.IO) {
            service.getSuggestions(request)
        }

        // Extract API suggestions
        val apiSuggestions = response.firstOrNull()?.generated_text
            ?.split(" ") // Split API response into words
            ?.filter { it.isNotBlank() }
            ?.take(5) ?: emptyList()

        // Combine and prioritize suggestions
        suggestions.value = (localSuggestions + apiSuggestions).distinct()

    } catch (e: Exception) {
        Log.e("fetchHybridSuggestions", "Error fetching API suggestions: ${e.message}")
        suggestions.value = localSuggestions // Fallback to local suggestions
    }
}

@Composable
fun PredictiveTextAppScreen() {
    val textState = remember { mutableStateOf("") }
    val suggestions = remember { mutableStateOf(listOf<String>()) }
    val coroutineScope = rememberCoroutineScope()

    // Example dictionary
    val dictionary = listOf(
        // Greetings and Common Phrases
        "hello", "hi", "hey", "good morning", "good afternoon", "good evening",
        "how are you?", "nice to meet you", "thank you", "thanks", "please", "sorry", "excuse me",

        // Questions and Interrogatives
        "who", "what", "where", "when", "why", "how", "which", "can", "could", "would", "should",
        "do", "does", "is", "are", "was", "were",

        // Affirmative and Negative Responses
        "yes", "no", "maybe", "of course", "definitely", "absolutely", "no problem", "not really",
        "I don’t know", "I think so", "I hope so", "sure", "okay", "alright",

        // Connecting Words and Fillers
        "and", "but", "or", "because", "so", "therefore", "although", "however", "in addition",
        "moreover", "besides", "actually", "basically", "just", "anyway",

        // Common Verbs
        "be", "have", "do", "say", "get", "make", "go", "know", "think", "take", "see", "come",
        "want", "use", "find", "give", "tell", "work", "call", "feel", "try", "leave", "put",
        "mean", "keep",

        // Adjectives for Common Expressions
        "good", "great", "amazing", "wonderful", "beautiful", "excellent", "bad", "sad", "happy",
        "excited", "boring", "interesting", "fun", "hard", "easy", "important", "quick", "fast",
        "slow", "new", "old",

        // Time-Related Words and Phrases
        "today", "tomorrow", "yesterday", "now", "later", "soon", "last week", "next week",
        "this week", "weekend", "morning", "afternoon", "evening", "night", "day", "year", "month",

        // Places and Locations
        "home", "office", "work", "school", "university", "library", "park", "restaurant", "cafe",
        "airport", "hotel", "mall", "beach", "gym", "hospital", "city", "country",

        // Frequently Used Nouns
        "time", "people", "person", "place", "thing", "problem", "solution", "idea", "experience",
        "story", "question", "answer", "information", "news", "friend", "family", "child", "work",
        "job", "money", "love", "life",

        // Numbers and Quantifiers
        "one", "two", "three", "four", "five", "many", "few", "some", "a lot", "several", "all",
        "none", "half", "most", "less", "more", "enough",

        // Expressions of Politeness and Emotion
        "please", "thank you", "sorry", "excuse me", "congratulations", "well done", "best wishes",
        "good luck", "happy birthday", "take care", "I miss you", "I love you", "I’m proud of you",
        "good job", "have fun", "stay safe", "see you soon"
    )


    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = textState.value,
            onValueChange = { newText ->
                textState.value = newText

                // Fetch hybrid suggestions dynamically
                coroutineScope.launch {
                    fetchHybridSuggestions(newText, suggestions, dictionary)
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
