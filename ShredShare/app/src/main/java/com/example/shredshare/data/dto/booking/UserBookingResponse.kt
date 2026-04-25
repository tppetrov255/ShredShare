package com.example.shredshare.data.dto

data class UserBookingResponse(
    val bookingId: Int,
    val startDate: String,
    val endDate: String,
    val totalPrice: Double,
    val status: String,
    val items: List<UserBookingItemResponse>
)