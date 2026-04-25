package com.example.shredshare.data.dto.auth

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val role: String?,
    val userId: Int?
)