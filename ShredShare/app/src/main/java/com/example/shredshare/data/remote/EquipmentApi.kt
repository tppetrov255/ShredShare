package com.example.shredshare.data.remote

import com.example.shredshare.data.dto.adminData.SimpleResponse
import com.example.shredshare.data.dto.equipment.EquipmentResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface EquipmentApi {

    @Multipart
    @POST("equipment/create")
    suspend fun createEquipment(
        @Part("wardrobeId") wardrobeId: RequestBody,
        @Part("name") name: RequestBody,
        @Part("type") type: RequestBody?,
        @Part("brand") brand: RequestBody?,
        @Part("size") size: RequestBody?,
        @Part("pricePerDay") pricePerDay: RequestBody?,
        @Part("quantity") quantity: RequestBody?,
        @Part("description") description: RequestBody?,
        @Part image: MultipartBody.Part?
    ): Response<SimpleResponse>

    @GET("equipment/wardrobe/{wardrobeId}")
    suspend fun getEquipmentByWardrobe(
        @Path("wardrobeId") wardrobeId: Int
    ): Response<List<EquipmentResponseDto>>

    @GET("equipment/{equipmentId}")
    suspend fun getEquipmentById(
        @Path("equipmentId") equipmentId: Int
    ): Response<EquipmentResponseDto>

    @Multipart
    @PUT("equipment/update/{equipmentId}")
    suspend fun updateEquipment(
        @Path("equipmentId") equipmentId: Int,
        @Part("name") name: RequestBody,
        @Part("type") type: RequestBody?,
        @Part("brand") brand: RequestBody?,
        @Part("size") size: RequestBody?,
        @Part("pricePerDay") pricePerDay: RequestBody?,
        @Part("quantity") quantity: RequestBody?,
        @Part("description") description: RequestBody?,
        @Part image: MultipartBody.Part?
    ): Response<SimpleResponse>

    @DELETE("equipment/delete/{equipmentId}")
    suspend fun deleteEquipment(
        @Path("equipmentId") equipmentId: Int
    ): Response<SimpleResponse>
}