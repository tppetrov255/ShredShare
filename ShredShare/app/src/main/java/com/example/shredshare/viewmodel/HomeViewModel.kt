package com.example.shredshare.viewmodel

import com.example.shredshare.data.dto.resort.Resort
import com.example.shredshare.data.remote.RetrofitInstance
import com.example.shredshare.data.repository.ResortRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val resorts: List<Resort> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class HomeViewModel : ViewModel() {

    private val repository = ResortRepository(RetrofitInstance.resortApi)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadResorts()
    }

    fun loadResorts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = repository.getAllResorts()

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(
                    resorts = result.getOrDefault(emptyList()),
                    isLoading = false,
                    error = null
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Unknown error"
                )
            }
        }
    }
}