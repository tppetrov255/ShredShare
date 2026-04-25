package com.example.shredshare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shredshare.data.repository.WardrobeRepository
import com.example.shredshare.data.dto.equipment.WardrobeListEquipmentDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WardrobeListViewModel : ViewModel() {

    private val repository = WardrobeRepository()

    data class WardrobeListUiState(
        val isLoading: Boolean = false,
        val wardrobes: List<WardrobeListEquipmentDto> = emptyList(),
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(WardrobeListUiState())
    val uiState: StateFlow<WardrobeListUiState> = _uiState.asStateFlow()

    fun loadWardrobes(resortId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            val result = repository.getApprovedWardrobesByResort(resortId)

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