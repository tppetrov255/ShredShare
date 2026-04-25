package com.example.shredshare.data.remote

import com.example.shredshare.data.dto.wardrobe.WardrobeResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import com.example.shredshare.data.dto.equipment.WardrobeListEquipmentDto
import retrofit2.http.GET
import retrofit2.http.Path
interface WardrobeApi {

    @Multipart
    @POST("wardrobes/create")
    suspend fun createWardrobe(
        @Part("ownerId") ownerId: RequestBody,
        @Part("resortId") resortId: RequestBody,
        @Part("name") name: RequestBody,
        @Part("address") address: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("description") description: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part imageUrl: MultipartBody.Part?
    ): Response<WardrobeResponse>

    @GET("wardrobes/resort/{resortId}")
    suspend fun getApprovedWardrobesByResort(
        @Path("resortId") resortId: Int
    ): Response<List<WardrobeListEquipmentDto>>
}