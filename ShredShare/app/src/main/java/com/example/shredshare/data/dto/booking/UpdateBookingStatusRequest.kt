package com.example.shredshare.data.dto.booking

data class UpdateBookingStatusRequest(
    val ownerId: Int,
    val newStatus: String
)