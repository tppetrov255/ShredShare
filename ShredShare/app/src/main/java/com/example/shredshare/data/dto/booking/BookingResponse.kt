package com.example.shredshare.data.dto.booking

data class BookingResponse(
    val success: Boolean,
    val message: String,
    val bookingId: Int?,
    val totalPrice: Double?
)