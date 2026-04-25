package com.example.shredshare.data.dto.adminData

data class AdminWardrobeRequestDto(
    val wardrobeId: Int,
    val wardrobeName: String,
    val ownerName: String,
    val resortName: String,
    val phone: String,
    val address: String,
    val description: String,
    val imageUrl: String?,
    val status: String
)