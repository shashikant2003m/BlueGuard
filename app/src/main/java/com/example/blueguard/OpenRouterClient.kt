package com.example.blueguard.data

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object OpenRouterClient {
    private val gson = GsonBuilder()
        .setLenient()
        .create()

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://openrouter.ai/api/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val api: OpenRouterApi = retrofit.create(OpenRouterApi::class.java)
}
