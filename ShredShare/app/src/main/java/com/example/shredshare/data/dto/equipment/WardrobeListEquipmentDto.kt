package com.example.shredshare.data.dto.equipment

data class WardrobeListEquipmentDto(
    val wardrobeId: Int,
    val name: String,
    val resortName: String,
    val address: String,
    val phone: String,
    val description: String,
    val imageUrl: String?,
    val latitude: Double?,
    val longitude: Double?
)