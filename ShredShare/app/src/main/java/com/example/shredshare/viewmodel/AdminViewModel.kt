package com.example.shredshare.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shredshare.data.repository.AdminRepository
import com.example.shredshare.ui.theme.screens.AdminWardrobeRequestUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {

    private val repository = AdminRepository()

    data class AdminUiState(
        val isLoading: Boolean = false,
        val pendingRequests: List<AdminWardrobeRequestUi> = emptyList(),
        val totalPending: Int = 0,
        val totalApproved: Int = 0,
        val totalOwners: Int = 0,
        val totalUsers: Int = 0,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    fun loadPendingRequests() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = repository.getPendingWardrobes()

            if (result != null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    pendingRequests = result,
                    totalPending = result.size
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Неуспешно зареждане на чакащите заявки"
                )
            }
        }
    }

    fun loadStats() {
        viewModelScope.launch {
            val stats = repository.getAdminStats()
            if (stats != null) {
                _uiState.value = _uiState.value.copy(
                    totalPending = stats.totalPending,
                    totalApproved = stats.totalApproved,
                    totalOwners = stats.totalOwners,
                    totalUsers = stats.totalUsers
                )
            }
        }
    }

    fun approveWardrobe(id: Int) {
        viewModelScope.launch {
            val result = repository.approveWardrobe(id)
            if (result != null) {
                loadDashboard()
            }
        }
    }

    fun rejectWardrobe(id: Int) {
        viewModelScope.launch {
            val result = repository.rejectWardrobe(id)
            if (result != null) {
                loadDashboard()
            }
        }
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val pending = repository.getPendingWardrobes()
            val stats = repository.getAdminStats()

            if (pending != null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    pendingRequests = pending,
                    totalPending = stats?.totalPending ?: pending.size,
                    totalApproved = stats?.totalApproved ?: 0,
                    totalOwners = stats?.totalOwners ?: 0,
                    totalUsers = stats?.totalUsers ?: 0
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Неуспешно зареждане"
                )
            }
        }
    }
}