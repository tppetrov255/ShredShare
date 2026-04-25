package com.example.shredshare.data.dto.cart

data class CartItemDto(
    val equipmentId: Int,
    val wardrobeId: Int,
    val name: String,
    val type: String?,
    val brand: String?,
    val size: String?,
    val pricePerDay: Double,
    val imageUrl: String?,
    val quantity: Int
)