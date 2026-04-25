package com.example.shredshare.data.dto

data class ResetPasswordRequest(
    val email: String,
    val code: String,
    val newPassword: String
)