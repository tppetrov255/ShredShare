package com.example.shredshare.data.dto.profile

data class UserProfileResponse(
    val userId: Int,
    val firstName: String,
    val lastName: String,
    val address: String,
    val email: String,
    val phone: String?,
    val role: String
)