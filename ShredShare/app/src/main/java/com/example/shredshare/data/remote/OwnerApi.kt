package com.example.shredshare.data.remote

import retrofit2.Response
import com.example.shredshare.data.dto.wardrobe.OwnerWardrobeDto
import retrofit2.http.GET
import retrofit2.http.Path

interface OwnerApi {
    @GET("wardrobes/owner/{ownerId}")
    suspend fun getWardrobesByOwner(
        @Path("ownerId") ownerId: Int
    ): Response<List<OwnerWardrobeDto>>
}