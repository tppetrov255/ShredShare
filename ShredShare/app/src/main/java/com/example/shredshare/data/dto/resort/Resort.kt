package com.example.shredshare.data.dto.resort

data class Resort(
    val resortId: Int,
    val name: String,
    val country: String?,
    val latitude: Double,
    val longitude: Double,
    val season: String?,
    val beginnerSlopes: Int?,
    val intermediateSlopes: Int?,
    val difficultSlopes: Int?,
    val totalSlopes: Int?,
    val childFriendly: String?,
    val snowparks: String?,
    val gondola: String?,
    val highestPoint: Int?
)