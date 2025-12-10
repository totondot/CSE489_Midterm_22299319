package com.example.a22299319_midterm_submission.models

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    // Read [cite: 130]
    @GET("api.php")
    fun getLandmarks(): Call<List<Landmark>>

    // Create [cite: 136]
    @Multipart
    @POST("api.php")
    fun createLandmark(
        @Part("title") title: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<Any> // Response is a generic JSON object
    // 4. UPDATE Request (Text only for stability)
    @FormUrlEncoded
    @PUT("api.php")
    fun updateLandmark(
        @Field("id") id: Int,
        @Field("title") title: String,
        @Field("lat") lat: Double,
        @Field("lon") lon: Double
    ): Call<Any>
    // Delete [cite: 132]
    @DELETE("api.php")
    fun deleteLandmark(@Query("id") id: Int): Call<Void>
}