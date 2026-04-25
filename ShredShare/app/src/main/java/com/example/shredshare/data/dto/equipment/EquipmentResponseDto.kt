package com.example.shredshare.data.dto.equipment

data class EquipmentResponseDto(
    val equipmentId: Int,
    val wardrobeId: Int,
    val name: String,
    val type: String?,
    val brand: String?,
    val size: String?,
    val pricePerDay: Double?,
    val quantity: Int?,
    val description: String?,
    val imageUrl: String?
)