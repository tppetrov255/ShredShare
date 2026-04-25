package com.example.shredshare.data.dto.booking

data class CreateBookingRequest(
    val userId: Int,
    val startDate: String,
    val endDate: String,
    val items: List<CreateBookingItemRequest>
)