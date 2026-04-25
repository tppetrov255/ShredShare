package com.example.shredshare.data.dto.wardrobe

data class OwnerWardrobeDto(
    val wardrobeId: Int,
    val name: String,
    val resortName: String,
    val address: String,
    val phone: String,
    val description: String,
    val imageUrl: String?,
    val status: String
)