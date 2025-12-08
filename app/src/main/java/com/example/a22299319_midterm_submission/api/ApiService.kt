package com.example.a22299319_midterm_submission.api

import com.example.a22299319_midterm_submission.models.Landmark
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    // 1. GET Request to fetch all landmarks
    @GET("api.php")
    fun getLandmarks(): Call<List<Landmark>>

    // 2. POST Request to upload a new landmark with an image
    @Multipart
    @POST("api.php")
    fun createLandmark(
        @Part("title") title: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<Any>

    // 3. DELETE Request to remove a landmark
    @DELETE("api.php")
    fun deleteLandmark(@Query("id") id: Int): Call<Void>
}