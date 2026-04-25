package com.example.shredshare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shredshare.data.repository.EquipmentRepository
import com.example.shredshare.data.dto.equipment.EquipmentResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EquipmentDetailsViewModel : ViewModel() {

    private val repository = EquipmentRepository()

    data class EquipmentDetailsUiState(
        val isLoading: Boolean = false,
        val item: EquipmentResponseDto? = null,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(EquipmentDetailsUiState())
    val uiState: StateFlow<EquipmentDetailsUiState> = _uiState.asStateFlow()

    fun loadEquipment(equipmentId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            val result = repository.getEquipmentById(equipmentId)

            if (result != null) {
                _uiState.value = EquipmentDetailsUiState(
                    isLoading = false,
                    item = result,
                    error = null
                )
            } else {
                _uiState.value = EquipmentDetailsUiState(
                    isLoading = false,
                    item = null,
                    error = "Неуспешно зареждане на артикула."
                )
            }
        }
    }
}