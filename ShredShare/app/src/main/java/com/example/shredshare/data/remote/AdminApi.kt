package com.example.shredshare.data.remote

import com.example.shredshare.data.dto.adminData.AdminDetailsDto
import com.example.shredshare.data.dto.adminData.AdminStatsResponse
import com.example.shredshare.data.dto.adminData.AdminWardrobeRequestDto
import com.example.shredshare.data.dto.wardrobe.WardrobeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface AdminApi {

    @GET("admin/wardrobes/pending")
    suspend fun getPendingWardrobes(): Response<List<AdminWardrobeRequestDto>>

    @PUT("admin/wardrobes/{id}/approve")
    suspend fun approveWardrobe(
        @Path("id") id: Int
    ): Response<WardrobeResponse>

    @PUT("admin/wardrobes/{id}/reject")
    suspend fun rejectWardrobe(
        @Path("id") id: Int
    ): Response<WardrobeResponse>

    @GET("admin/stats")
    suspend fun getAdminStats(): Response<AdminStatsResponse>

    @GET("admin/wardrobes/{id}")
    suspend fun getWardrobeDetails(
        @Path("id") id: Int
    ): Response<AdminDetailsDto>
}