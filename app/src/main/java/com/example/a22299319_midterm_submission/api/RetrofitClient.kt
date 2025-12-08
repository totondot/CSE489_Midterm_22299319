package com.example.a22299319_midterm_submission.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // URL from the Exam Question Paper
    private const val BASE_URL = "https://labs.anontech.info/cse489/t3/"

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}