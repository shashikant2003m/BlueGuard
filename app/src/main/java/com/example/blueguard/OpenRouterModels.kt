package com.example.blueguard.data

data class OpenRouterMessage(
    val role: String,
    val content: String
)

data class OpenRouterRequest(
    val model: String = "deepseek/deepseek-r1-0528:free",
    val messages: List<OpenRouterMessage>
)

data class OpenRouterChoice(
    val message: OpenRouterMessage
)

data class OpenRouterResponse(
    val choices: List<OpenRouterChoice>
)
