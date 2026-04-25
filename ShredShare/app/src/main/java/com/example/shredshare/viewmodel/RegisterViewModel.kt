package com.example.shredshare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shredshare.data.dto.auth.RegisterRequest
import com.example.shredshare.data.repository.AuthRepository
import com.example.shredshare.domain.validation.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val repository = AuthRepository()
    private val emailValidator = EmailValidator()
    private val passwordValidator = PasswordValidator()
    private val nameValidator = NameValidator()
    private val phoneValidator = PhoneValidator()
    private val addressValidator = AddressValidator()

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    data class RegisterUiState(
        val firstName: String = "",
        val lastName: String = "",
        val address: String = "",
        val email: String = "",
        val phone: String = "",
        val password: String = "",
        val confirmPassword: String = "",
        val passwordVisible: Boolean = false,
        val confirmPasswordVisible: Boolean = false,
        val isLoading: Boolean = false,
        val error: String? = null,
        val success: Boolean = false,
        val userId: Int? = null,
        val registeredRole: String? = null,
        val validationErrors: Map<String, String> = emptyMap()
    )

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

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value)
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = value)
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(passwordVisible = !_uiState.value.passwordVisible)
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.value = _uiState.value.copy(confirmPasswordVisible = !_uiState.value.confirmPasswordVisible)
    }

    private fun validate(): Boolean {
        val state = _uiState.value
        val errors = mutableMapOf<String, String>()

        val firstNameResult = nameValidator.validate(state.firstName, "Име")
        if (firstNameResult is ValidationResult.Error) errors["firstName"] = firstNameResult.errorMessage

        val lastNameResult = nameValidator.validate(state.lastName, "Фамилия")
        if (lastNameResult is ValidationResult.Error) errors["lastName"] = lastNameResult.errorMessage

        val addressResult = addressValidator.validate(state.address)
        if (addressResult is ValidationResult.Error) errors["address"] = addressResult.errorMessage

        val emailResult = emailValidator.validate(state.email)
        if (emailResult is ValidationResult.Error) errors["email"] = emailResult.errorMessage

        val phoneResult = phoneValidator.validate(state.phone)
        if (phoneResult is ValidationResult.Error) errors["phone"] = phoneResult.errorMessage

        val passwordResult = passwordValidator.validate(state.password)
        if (passwordResult is ValidationResult.Error) errors["password"] = passwordResult.errorMessage

        val confirmPasswordResult = passwordValidator.validateMatching(state.password, state.confirmPassword)
        if (confirmPasswordResult is ValidationResult.Error) errors["confirmPassword"] = confirmPasswordResult.errorMessage

        _uiState.value = _uiState.value.copy(validationErrors = errors)
        return errors.isEmpty()
    }

    fun register(role: String) {
        if (!validate()) return

        val state = _uiState.value
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val normalizedRole = role.lowercase().trim()

            val request = RegisterRequest(
                firstName = state.firstName.trim(),
                lastName = state.lastName.trim(),
                address = state.address.trim(),
                email = state.email.trim(),
                phone = state.phone.trim(),
                password = state.password,
                role = normalizedRole
            )

            try {
                val registerResult = repository.register(request)

                if (registerResult?.success == true) {
                    kotlinx.coroutines.delay(300)
                    val loginResult = repository.login(request.email, request.password)
                    val finalUserId = loginResult?.userId ?: registerResult.userId

                    if (finalUserId != null && finalUserId > 0) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            success = true,
                            userId = finalUserId,
                            registeredRole = normalizedRole,
                            error = null
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            success = false,
                            error = "Регистрацията е успешна, но липсва потребителско ID от сървъра."
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        success = false,
                        error = registerResult?.message ?: "Имейлът вече е зает."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Грешка при връзката."
                )
            }
        }
    }
}
