package com.example.shredshare.data.dto

data class OwnerBookingResponse(
    val bookingId: Int,
    val customerId: Int?,
    val customerName: String,
    val customerEmail: String,
    val customerPhone: String?,
    val customerAddress: String?,
    val startDate: String,
    val endDate: String,
    val totalPrice: Double,
    val status: String,
    val items: List<OwnerBookingItemResponse>
)