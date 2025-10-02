package com.example.blueguard.data

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenRouterApi {
    @POST("v1/chat/completions")
    fun getChatCompletion(
        @Header("Authorization") token: String,
        @Header("Referer") referer: String? = null,
        @Body request: OpenRouterRequest
    ): Call<ResponseBody>

}
