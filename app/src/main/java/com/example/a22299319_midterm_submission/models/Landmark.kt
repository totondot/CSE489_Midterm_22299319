package com.example.a22299319_midterm_submission.models

import com.google.gson.annotations.SerializedName

data class Landmark(
    // ID might be missing or a string in some APIs
    val id: Any?,

    // Accept "title", "name", or "landmark_name"
    @SerializedName(value = "title", alternate = ["name", "landmark_name"])
    val title: String?,

    // API might send "lat" as a String ("23.5") or Double (23.5)
    // We accept Any, then convert it manually
    @SerializedName(value = "lat", alternate = ["latitude"])
    val _lat: Any?,

    @SerializedName(value = "lon", alternate = ["longitude", "long"])
    val _lon: Any?,

    // Accept "image", "img", or "photo"
    @SerializedName(value = "image", alternate = ["img", "photo_url"])
    val image: String?
) {
    // Helper to safely get Double values
    val lat: Double
        get() = _lat.toString().toDoubleOrNull() ?: 0.0

    val lon: Double
        get() = _lon.toString().toDoubleOrNull() ?: 0.0
}