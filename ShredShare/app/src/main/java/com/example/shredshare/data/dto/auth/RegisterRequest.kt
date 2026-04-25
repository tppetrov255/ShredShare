package com.example.shredshare.data.dto.auth

data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val address: String,
    val email: String,
    val phone: String,
    val password: String,
    val role: String
)