package com.example.shredshare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shredshare.data.repository.AuthRepository
import com.example.shredshare.domain.validation.NameValidator
import com.example.shredshare.domain.validation.PasswordValidator
import com.example.shredshare.domain.validation.ValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ResetPasswordViewModel : ViewModel() {

    private val repository = AuthRepository()
    private val passwordValidator = PasswordValidator()
    private val codeValidator = NameValidator()

    data class ResetPasswordUiState(
        val code: String = "",
        val newPassword: String = "",
        val confirmPassword: String = "",
        val isLoading: Boolean = false,
        val message: String? = null,
        val error: String? = null,
        val success: Boolean = false
    )

    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    fun onCodeChange(value: String) {
        _uiState.value = _uiState.value.copy(code = value, error = null, message = null, success = false)
    }

    fun onNewPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(newPassword = value, error = null, message = null, success = false)
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = value, error = null, message = null, success = false)
    }

    private fun validate(): Boolean {
        val state = _uiState.value
        
        val codeRes = codeValidator.validate(state.code, "Кодът")
        if (codeRes is ValidationResult.Error) {
            _uiState.value = state.copy(error = codeRes.errorMessage)
            return false
        }

        val passRes = passwordValidator.validate(state.newPassword)
        if (passRes is ValidationResult.Error) {
            _uiState.value = state.copy(error = passRes.errorMessage)
            return false
        }

        val confirmRes = passwordValidator.validateMatching(state.newPassword, state.confirmPassword)
        if (confirmRes is ValidationResult.Error) {
            _uiState.value = state.copy(error = confirmRes.errorMessage)
            return false
        }

        return true
    }

    fun resetPassword(email: String) {
        if (!validate()) return

        val state = _uiState.value

        viewModelScope.launch {
            _uiState.value = state.copy(
                isLoading = true,
                error = null,
                message = null,
                success = false
            )

            val result = repository.resetPassword(
                email = email,
                code = state.code.trim(),
                newPassword = state.newPassword
            )

            if (result?.success == true) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = result.message,
                    success = true
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result?.message ?: "Неуспешна смяна на паролата."
                )
            }
        }
    }
}
