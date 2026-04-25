package com.example.shredshare.data.dto.wardrobe

data class CreateWardrobeRequest(
    val ownerId: Int,
    val resortId: Int,
    val name: String,
    val address: String,
    val phone: String,
    val description: String,
    val latitude: Double?,
    val longitude: Double?
)