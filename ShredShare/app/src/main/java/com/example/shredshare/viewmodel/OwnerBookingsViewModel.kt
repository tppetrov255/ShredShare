package com.example.shredshare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shredshare.data.repository.BookingRepository
import com.example.shredshare.data.dto.OwnerBookingResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OwnerBookingsViewModel : ViewModel() {

    private val repository = BookingRepository()

    data class OwnerBookingsUiState(
        val isLoading: Boolean = false,
        val bookings: List<OwnerBookingResponse> = emptyList(),
        val error: String? = null,
        val message: String? = null
    )

    private val _uiState = MutableStateFlow(OwnerBookingsUiState())
    val uiState: StateFlow<OwnerBookingsUiState> = _uiState.asStateFlow()

    fun loadBookings(ownerId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            val result = repository.getBookingsByOwner(ownerId)

            if (result != null) {
                _uiState.value = OwnerBookingsUiState(
                    isLoading = false,
                    bookings = result,
                    error = null,
                    message = _uiState.value.message
                )
            } else {
                _uiState.value = OwnerBookingsUiState(
                    isLoading = false,
                    bookings = emptyList(),
                    error = "Неуспешно зареждане на поръчките.",
                    message = null
                )
            }
        }
    }

    fun updateBookingStatus(ownerId: Int, bookingId: Int, newStatus: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                message = null
            )

            val result = repository.updateBookingStatusByOwner(
                bookingId = bookingId,
                ownerId = ownerId,
                newStatus = newStatus
            )

            if (result?.success == true) {
                val refreshed = repository.getBookingsByOwner(ownerId).orEmpty()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    bookings = refreshed,
                    message = result.message,
                    error = null
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result?.message ?: "Неуспешна промяна на статуса."
                )
            }
        }
    }


    fun clearMessage() {
        _uiState.value = _uiState.value.copy(
            error = null,
            message = null
        )
    }
}