package com.example.a22299319_midterm_submission.models

data class Landmark(
    val id: Int?, // ID is null when creating new
    val title: String,
    val lat: Double,
    val lon: Double,
    val image: String? // URL string from API
)