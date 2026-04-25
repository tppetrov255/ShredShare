package com.example.shredshare.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.shredshare.data.repository.BookingRepository
import com.example.shredshare.data.dto.cart.CartItemDto
import com.example.shredshare.data.dto.booking.CreateBookingItemRequest
import com.example.shredshare.data.dto.booking.CreateBookingRequest
import com.example.shredshare.data.dto.equipment.EquipmentResponseDto
import com.example.shredshare.data.local.CartManager
import com.example.shredshare.data.local.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val cartManager = CartManager(application)
    private val sessionManager = SessionManager(application)
    private val bookingRepository = BookingRepository()

    data class CartUiState(
        val items: List<CartItemDto> = emptyList(),
        val startDate: String = "",
        val endDate: String = "",
        val isLoading: Boolean = false,
        val error: String? = null,
        val message: String? = null
    ) {
        val isEmpty: Boolean get() = items.isEmpty()
        val totalPrice: Double get() = items.sumOf { it.pricePerDay * it.quantity }
    }

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        loadCart()
    }

    fun loadCart() {
        _uiState.value = _uiState.value.copy(items = cartManager.getCartItems())
    }

    fun addEquipmentToCart(item: EquipmentResponseDto) {
        val price = item.pricePerDay ?: 0.0

        val cartItem = CartItemDto(
            equipmentId = item.equipmentId,
            wardrobeId = item.wardrobeId,
            name = item.name,
            type = item.type,
            brand = item.brand,
            size = item.size,
            pricePerDay = price,
            imageUrl = item.imageUrl,
            quantity = 1
        )

        cartManager.addItem(cartItem)
        loadCart()
        _uiState.value = _uiState.value.copy(message = "Артикулът е добавен в количката.")
    }

    fun increaseQuantity(equipmentId: Int) {
        cartManager.increaseQuantity(equipmentId)
        loadCart()
    }

    fun decreaseQuantity(equipmentId: Int) {
        cartManager.decreaseQuantity(equipmentId)
        loadCart()
    }

    fun removeItem(equipmentId: Int) {
        cartManager.removeItem(equipmentId)
        loadCart()
    }

    fun clearCart() {
        cartManager.clearCart()
        loadCart()
    }

    fun onStartDateChange(value: String) {
        _uiState.value = _uiState.value.copy(startDate = value)
    }

    fun onEndDateChange(value: String) {
        _uiState.value = _uiState.value.copy(endDate = value)
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null, error = null)
    }

    fun submitBooking(onSuccess: () -> Unit = {}) {
        val current = _uiState.value
        val userId = sessionManager.getUserId()

        if (userId == -1) {
            _uiState.value = current.copy(error = "Няма активна сесия.")
            return
        }

        if (current.items.isEmpty()) {
            _uiState.value = current.copy(error = "Количката е празна.")
            return
        }

        if (current.startDate.isBlank() || current.endDate.isBlank()) {
            _uiState.value = current.copy(error = "Моля, въведете начална и крайна дата.")
            return
        }

        viewModelScope.launch {
            _uiState.value = current.copy(isLoading = true, error = null, message = null)

            val request = CreateBookingRequest(
                userId = userId,
                startDate = current.startDate,
                endDate = current.endDate,
                items = current.items.map {
                    CreateBookingItemRequest(
                        equipmentId = it.equipmentId,
                        quantity = it.quantity
                    )
                }
            )

            val result = bookingRepository.createBooking(request)

            if (result?.success == true) {
                cartManager.clearCart()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    items = emptyList(),
                    message = result.message
                )
                onSuccess()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result?.message ?: "Неуспешно създаване на резервация."
                )
            }
        }
    }
}