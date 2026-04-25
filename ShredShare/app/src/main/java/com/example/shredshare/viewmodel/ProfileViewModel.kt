package com.example.shredshare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shredshare.data.repository.UserRepository
import com.example.shredshare.data.dto.profile.UpdateProfileRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val repository = UserRepository()

    data class ProfileUiState(
        val isLoading: Boolean = false,
        val isSaving: Boolean = false,
        val firstName: String = "",
        val lastName: String = "",
        val address: String = "",
        val email: String = "",
        val phone: String = "",
        val role: String = "",
        val error: String? = null,
        val message: String? = null,
        val saveSuccess: Boolean = false
    )

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadProfile(userId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                message = null
            )

            val result = repository.getUserProfile(userId)

            if (result != null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    firstName = result.firstName,
                    lastName = result.lastName,
                    address = result.address,
                    email = result.email,
                    phone = result.phone.orEmpty(),
                    role = result.role,
                    error = null
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Неуспешно зареждане на профила."
                )
            }
        }
    }

    fun onFirstNameChange(value: String) {
        _uiState.value = _uiState.value.copy(firstName = value)
    }

    fun onLastNameChange(value: String) {
        _uiState.value = _uiState.value.copy(lastName = value)
    }

    fun onAddressChange(value: String) {
        _uiState.value = _uiState.value.copy(address = value)
    }

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value)
    }

    fun onPhoneChange(value: String) {
        _uiState.value = _uiState.value.copy(phone = value)
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(
            message = null,
            error = null,
            saveSuccess = false
        )
    }

    fun saveProfile(userId: Int) {
        val current = _uiState.value

        val firstName = current.firstName.trim()
        val lastName = current.lastName.trim()
        val address = current.address.trim()
        val email = current.email.trim()
        val phone = current.phone.trim()

        if (firstName.isBlank()) {
            _uiState.value = current.copy(error = "Името е задължително")
            return
        }

        if (lastName.isBlank()) {
            _uiState.value = current.copy(error = "Фамилията е задължителна")
            return
        }

        if (address.isBlank()) {
            _uiState.value = current.copy(error = "Адресът е задължителен")
            return
        }

        if (email.isBlank()) {
            _uiState.value = current.copy(error = "Имейлът е задължителен")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = current.copy(error = "Невалиден имейл")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSaving = true,
                error = null,
                message = null,
                saveSuccess = false
            )

            val result = repository.updateUserProfile(
                userId = userId,
                request = UpdateProfileRequest(
                    firstName = firstName,
                    lastName = lastName,
                    address = address,
                    email = email,
                    phone = phone.ifBlank { null }
                )
            )

            if (result?.success == true) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    message = result.message,
                    saveSuccess = true
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = result?.message ?: "Неуспешно запазване на профила."
                )
            }
        }
    }
}