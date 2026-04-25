package com.example.shredshare.data.repository

import com.example.shredshare.data.dto.adminData.AdminStatsResponse
import com.example.shredshare.data.remote.RetrofitInstance
import com.example.shredshare.data.dto.wardrobe.WardrobeResponse
import com.example.shredshare.ui.theme.screens.AdminWardrobeRequestUi

class AdminRepository {

    suspend fun getPendingWardrobes(): List<AdminWardrobeRequestUi>? {
        return try {
            val response = RetrofitInstance.adminApi.getPendingWardrobes()
            if (response.isSuccessful) {
                response.body()?.map {
                    AdminWardrobeRequestUi(
                        id = it.wardrobeId,
                        wardrobeName = it.wardrobeName,
                        ownerName = it.ownerName,
                        resortName = it.resortName,
                        phone = it.phone ?: "",
                        address = it.address ?: "",
                        description = it.description ?: "",
                        status = it.status
                    )
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun approveWardrobe(id: Int): WardrobeResponse? {
        return try {
            val response = RetrofitInstance.adminApi.approveWardrobe(id)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun rejectWardrobe(id: Int): WardrobeResponse? {
        return try {
            val response = RetrofitInstance.adminApi.rejectWardrobe(id)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getAdminStats(): AdminStatsResponse? {
        return try {
            val response = RetrofitInstance.adminApi.getAdminStats()
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getWardrobeDetails(id: Int): com.example.shredshare.data.dto.adminData.AdminDetailsDto? {
        return try {
            val response = RetrofitInstance.adminApi.getWardrobeDetails(id)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }
}