package com.example.shredshare.data.dto

data class OwnerBookingItemResponse(
    val equipmentId: Int,
    val equipmentName: String,
    val quantity: Int,
    val unitPrice: Double,
    val wardrobeName: String
)