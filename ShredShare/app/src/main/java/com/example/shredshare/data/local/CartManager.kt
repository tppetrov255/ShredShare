package com.example.shredshare.data.local

import android.content.Context
import com.example.shredshare.data.dto.cart.CartItemDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CartManager(context: Context) {

    private val prefs = context.getSharedPreferences("shredshare_cart", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val CART_KEY = "cart_items"
    }

    fun getCartItems(): List<CartItemDto> {
        val json = prefs.getString(CART_KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<CartItemDto>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    private fun saveCartItems(items: List<CartItemDto>) {
        prefs.edit().putString(CART_KEY, gson.toJson(items)).apply()
    }

    fun addItem(item: CartItemDto) {
        val current = getCartItems().toMutableList()
        val index = current.indexOfFirst { it.equipmentId == item.equipmentId }

        if (index != -1) {
            val existing = current[index]
            current[index] = existing.copy(quantity = existing.quantity + item.quantity)
        } else {
            current.add(item)
        }

        saveCartItems(current)
    }

    fun increaseQuantity(equipmentId: Int) {
        val updated = getCartItems().map {
            if (it.equipmentId == equipmentId) it.copy(quantity = it.quantity + 1) else it
        }
        saveCartItems(updated)
    }

    fun decreaseQuantity(equipmentId: Int) {
        val updated = getCartItems().mapNotNull {
            if (it.equipmentId == equipmentId) {
                val newQuantity = it.quantity - 1
                if (newQuantity <= 0) null else it.copy(quantity = newQuantity)
            } else {
                it
            }
        }
        saveCartItems(updated)
    }

    fun removeItem(equipmentId: Int) {
        val updated = getCartItems().filterNot { it.equipmentId == equipmentId }
        saveCartItems(updated)
    }

    fun clearCart() {
        prefs.edit().remove(CART_KEY).apply()
    }
}