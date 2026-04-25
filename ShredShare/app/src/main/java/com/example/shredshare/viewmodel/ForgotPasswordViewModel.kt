package com.example.shredshare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shredshare.data.repository.AuthRepository
import com.example.shredshare.domain.validation.EmailValidator
import com.example.shredshare.domain.validation.ValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel : ViewModel() {

    private val repository = AuthRepository()
    private val emailValidator = EmailValidator()

    data class ForgotPasswordUiState(
        val email: String = "",
        val isLoading: Boolean = false,
        val message: String? = null,
        val error: String? = null,
        val success: Boolean = false
    )

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(
            email = value,
            error = null,
            message = null,
            success = false
        )
    }

    private fun validate(): Boolean {
        val state = _uiState.value
        val result = emailValidator.validate(state.email)
        
        if (result is ValidationResult.Error) {
            _uiState.value = state.copy(error = result.errorMessage)
            return false
        }
        return true
    }

    fun sendResetCode() {
        if (!validate()) return

        val email = _uiState.value.email.trim()

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                message = null,
                success = false
            )

            val result = repository.forgotPassword(email)

            if (result?.success == true) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = result.message,
                    success = true
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result?.message ?: "Неуспешно изпращане на код."
                )
            }
        }
    }
}
