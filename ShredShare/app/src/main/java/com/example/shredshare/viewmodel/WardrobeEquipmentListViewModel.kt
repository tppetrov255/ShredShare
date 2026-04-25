package com.example.shredshare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shredshare.data.repository.EquipmentRepository
import com.example.shredshare.data.dto.equipment.EquipmentResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WardrobeEquipmentListViewModel : ViewModel() {

    private val repository = EquipmentRepository()

    data class WardrobeEquipmentListUiState(
        val isLoading: Boolean = false,
        val items: List<EquipmentResponseDto> = emptyList(),
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(WardrobeEquipmentListUiState())
    val uiState: StateFlow<WardrobeEquipmentListUiState> = _uiState.asStateFlow()

    fun loadEquipment(wardrobeId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            val result = repository.getEquipmentByWardrobe(wardrobeId)

            if (result != null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    items = result
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Неуспешно зареждане на артикулите"
                )
            }
        }
    }
}