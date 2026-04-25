package com.example.shredshare.data.repository

import com.example.shredshare.data.dto.login.LoginRequest
import com.example.shredshare.data.dto.login.LoginResponse
import com.example.shredshare.data.dto.auth.RegisterRequest
import com.example.shredshare.data.remote.RetrofitInstance
import com.example.shredshare.data.dto.adminData.SimpleResponse
import com.example.shredshare.data.dto.ForgotPasswordRequest
import com.example.shredshare.data.dto.auth.RegisterResponse
import com.example.shredshare.data.dto.ResetPasswordRequest
import com.google.gson.Gson

class AuthRepository {

    private val gson = Gson()

    suspend fun login(email: String, password: String): LoginResponse? {
        return try {
            val response = RetrofitInstance.authApi.login(
                LoginRequest(email, password)
            )
            if (response.isSuccessful) {
                response.body()
            } else {
                parseError(response.errorBody()?.string())
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun register(request: RegisterRequest): RegisterResponse? {
        return try {
            val response = RetrofitInstance.authApi.register(request)
            if (response.isSuccessful) {
                response.body()
            } else {
                parseRegisterError(response.errorBody()?.string())
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun parseRegisterError(errorJson: String?): RegisterResponse? {
        return try {
            gson.fromJson(errorJson, RegisterResponse::class.java)
        } catch (e: Exception) {
            null
        }
    }

    private fun parseError(errorJson: String?): LoginResponse? {
        return try {
            gson.fromJson(errorJson, LoginResponse::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun forgotPassword(email: String): SimpleResponse? {
        return try {
            val response = RetrofitInstance.authApi.forgotPassword(
                ForgotPasswordRequest(email = email)
            )

            if (response.isSuccessful) {
                response.body()
            } else {
                gson.fromJson(response.errorBody()?.string(), SimpleResponse::class.java)
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun resetPassword(
        email: String,
        code: String,
        newPassword: String
    ): SimpleResponse? {
        return try {
            val response = RetrofitInstance.authApi.resetPassword(
                ResetPasswordRequest(
                    email = email,
                    code = code,
                    newPassword = newPassword
                )
            )

            if (response.isSuccessful) {
                response.body()
            } else {
                gson.fromJson(response.errorBody()?.string(), SimpleResponse::class.java)
            }
        } catch (e: Exception) {
            null
        }
    }
}