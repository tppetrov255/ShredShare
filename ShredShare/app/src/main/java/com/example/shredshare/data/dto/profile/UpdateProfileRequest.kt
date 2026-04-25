package com.example.shredshare.data.dto.profile

data class UpdateProfileRequest(
    val firstName: String,
    val lastName: String,
    val address: String,
    val email: String,
    val phone: String?
)