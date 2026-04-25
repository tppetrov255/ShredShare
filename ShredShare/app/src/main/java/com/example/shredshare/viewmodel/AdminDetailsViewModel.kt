package com.example.shredshare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shredshare.data.repository.AdminRepository
import com.example.shredshare.data.dto.adminData.AdminDetailsDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminDetailsViewModel : ViewModel() {

    private val repository = AdminRepository()

    data class UiState(
        val isLoading: Boolean = false,
        val details: AdminDetailsDto? = null,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun loadDetails(wardrobeId: Int) {
        viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)

            val result = repository.getWardrobeDetails(wardrobeId)

            if (result != null) {
                _uiState.value = UiState(details = result)
            } else {
                _uiState.value = UiState(error = "Неуспешно зареждане на детайлите")
            }
        }
    }
}