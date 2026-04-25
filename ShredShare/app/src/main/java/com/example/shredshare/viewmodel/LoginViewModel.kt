package com.example.shredshare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shredshare.data.repository.AuthRepository
import com.example.shredshare.domain.validation.EmailValidator
import com.example.shredshare.domain.validation.PasswordValidator
import com.example.shredshare.domain.validation.ValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val repository = AuthRepository()
    private val emailValidator = EmailValidator()
    private val passwordValidator = PasswordValidator()

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    data class LoginUiState(
        val email: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        val error: String? = null,
        val success: Boolean = false,
        val role: String? = null,
        val userId: Int? = null
    )

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    private fun validate(): Boolean {
        val state = _uiState.value
        
        val emailResult = emailValidator.validate(state.email)
        if (emailResult is ValidationResult.Error) {
            _uiState.value = _uiState.value.copy(error = emailResult.errorMessage)
            return false
        }

        if (state.password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Паролата е задължителна")
            return false
        }

        return true
    }

    fun login() {
        if (!validate()) return

        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = repository.login(email, password)

            _uiState.value = if (result?.success == true) {
                _uiState.value.copy(
                    isLoading = false,
                    success = true,
                    error = null,
                    role = result.role,
                    userId = result.userId
                )
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    success = false,
                    error = result?.message ?: "Неуспешно влизане"
                )
            }
        }
    }
}
