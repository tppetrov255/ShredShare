package com.example.shredshare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shredshare.data.repository.OwnerRepository
import com.example.shredshare.ui.theme.screens.OwnerWardrobeUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OwnerViewModel : ViewModel() {

    private val repository = OwnerRepository()

    data class OwnerUiState(
        val isLoading: Boolean = false,
        val wardrobes: List<OwnerWardrobeUi> = emptyList(),
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(OwnerUiState())
    val uiState: StateFlow<OwnerUiState> = _uiState.asStateFlow()

    fun loadOwnerWardrobes(ownerId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            val result = repository.getWardrobesByOwner(ownerId)

            if (result != null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    wardrobes = result
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Неуспешно зареждане на гардеробите"
                )
            }
        }
    }
}