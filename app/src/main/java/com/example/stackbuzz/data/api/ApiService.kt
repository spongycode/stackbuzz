package com.example.stackbuzz.data.api

import com.example.stackbuzz.data.model.QuestionResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {
    @GET("2.3/questions?page=10&pagesize=100&order=desc&sort=creation&site=stackoverflow")
    fun getQuestions(): Call<QuestionResponse>

    object Companion {
        private const val BASE_URL = "https://api.stackexchange.com/"
        fun create(): ApiService {
            val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build()
            return retrofit.create(ApiService::class.java)
        }
    }
}