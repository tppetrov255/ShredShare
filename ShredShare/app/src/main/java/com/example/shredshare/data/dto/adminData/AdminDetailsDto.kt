package com.example.shredshare.data.dto.adminData


data class AdminDetailsDto(
    val wardrobeId: Int,
    val wardrobeName: String,
    val resortName: String,
    val phone: String?,
    val address: String?,
    val description: String?,
    val imageUrl: String?,
    val status: String,

    val ownerId: Int?,
    val ownerFirstName: String?,
    val ownerLastName: String?,
    val ownerEmail: String?,
    val ownerPhone: String?,
    val ownerRole: String?
)