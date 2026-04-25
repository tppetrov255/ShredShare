package com.example.shredshare.data.remote

import com.example.shredshare.data.dto.adminData.SimpleResponse
import com.example.shredshare.data.dto.ForgotPasswordRequest
import com.example.shredshare.data.dto.login.LoginRequest
import com.example.shredshare.data.dto.login.LoginResponse
import com.example.shredshare.data.dto.auth.RegisterRequest
import com.example.shredshare.data.dto.auth.RegisterResponse
import com.example.shredshare.data.dto.ResetPasswordRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): Response<SimpleResponse>

    @POST("auth/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<SimpleResponse>
}