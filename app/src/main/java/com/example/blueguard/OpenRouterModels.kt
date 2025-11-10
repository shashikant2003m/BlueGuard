package com.example.blueguard.data

data class OpenRouterMessage(
    val role: String,
    val content: String
)

data class OpenRouterRequest(
    val model: String = "openai/gpt-oss-20b:free",
    val messages: List<OpenRouterMessage>
)

data class OpenRouterChoice(
    val message: OpenRouterMessage
)

data class OpenRouterResponse(
    val choices: List<OpenRouterChoice>
)
