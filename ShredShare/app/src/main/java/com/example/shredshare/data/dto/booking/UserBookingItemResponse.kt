package com.example.shredshare.data.dto

data class UserBookingItemResponse(
    val equipmentId: Int,
    val equipmentName: String,
    val quantity: Int,
    val unitPrice: Double
)