package com.example.shredshare.data.repository

import com.example.shredshare.data.dto.resort.Resort
import com.example.shredshare.data.remote.ResortApi

class ResortRepository(
    private val resortApi: ResortApi
) {
    suspend fun getAllResorts(): Result<List<Resort>> {
        return try {
            val response = resortApi.getAllResorts()

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to load resorts"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}