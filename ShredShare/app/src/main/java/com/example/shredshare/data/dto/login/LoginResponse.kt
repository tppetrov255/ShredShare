package com.example.shredshare.data.dto.login

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val role: String?,
    val userId: Int?
)