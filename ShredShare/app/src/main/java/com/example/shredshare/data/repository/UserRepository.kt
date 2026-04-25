package com.example.shredshare.data.repository

import com.example.shredshare.data.remote.RetrofitInstance
import com.example.shredshare.data.dto.adminData.SimpleResponse
import com.example.shredshare.data.dto.profile.UpdateProfileRequest
import com.example.shredshare.data.dto.profile.UserProfileResponse
import com.google.gson.Gson

class UserRepository {

    private val gson = Gson()

    suspend fun getUserProfile(userId: Int): UserProfileResponse? {
        return try {
            val response = RetrofitInstance.userApi.getUserProfile(userId)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateUserProfile(
        userId: Int,
        request: UpdateProfileRequest
    ): SimpleResponse? {
        return try {
            val response = RetrofitInstance.userApi.updateUserProfile(userId, request)
            if (response.isSuccessful) {
                response.body()
            } else {
                parseError(response.errorBody()?.string())
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun parseError(errorJson: String?): SimpleResponse? {
        return try {
            gson.fromJson(errorJson, SimpleResponse::class.java)
        } catch (e: Exception) {
            null
        }
    }
}