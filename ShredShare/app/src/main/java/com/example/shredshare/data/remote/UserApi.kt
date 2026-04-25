package com.example.shredshare.data.remote

import com.example.shredshare.data.dto.adminData.SimpleResponse
import com.example.shredshare.data.dto.profile.UpdateProfileRequest
import com.example.shredshare.data.dto.profile.UserProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApi {

    @GET("users/{userId}")
    suspend fun getUserProfile(
        @Path("userId") userId: Int
    ): Response<UserProfileResponse>

    @PUT("users/{userId}")
    suspend fun updateUserProfile(
        @Path("userId") userId: Int,
        @Body request: UpdateProfileRequest
    ): Response<SimpleResponse>
}