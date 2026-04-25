package com.example.shredshare.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.shredshare.data.repository.BookingRepository
import com.example.shredshare.data.dto.UserBookingResponse
import com.example.shredshare.data.local.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BookingRepository()
    private val sessionManager = SessionManager(application)

    data class BookingsUiState(
        val isLoading: Boolean = false,
        val bookings: List<UserBookingResponse> = emptyList(),
        val error: String? = null,
        val message: String? = null
    )

    private val _uiState = MutableStateFlow(BookingsUiState())
    val uiState: StateFlow<BookingsUiState> = _uiState.asStateFlow()

    fun loadBookings(userId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                message = null
            )

            val result = repository.getBookingsByUser(userId)

            if (result != null) {
                _uiState.value = BookingsUiState(
                    isLoading = false,
                    bookings = result,
                    error = null,
                    message = null
                )
            } else {
                _uiState.value = BookingsUiState(
                    isLoading = false,
                    bookings = emptyList(),
                    error = "Неуспешно зареждане на резервациите.",
                    message = null
                )
            }
        }
    }

    fun cancelBooking(bookingId: Int) {
        val userId = sessionManager.getUserId()

        if (userId == -1) {
            _uiState.value = _uiState.value.copy(error = "Няма активна сесия.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                message = null
            )

            val result = repository.cancelBooking(bookingId, userId)

            if (result?.success == true) {
                val updatedBookings = repository.getBookingsByUser(userId)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    bookings = updatedBookings ?: emptyList(),
                    message = result.message,
                    error = null
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result?.message ?: "Неуспешно отказване на резервацията."
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