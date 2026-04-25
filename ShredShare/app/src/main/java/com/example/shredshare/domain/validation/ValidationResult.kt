package com.example.shredshare.domain.validation

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val errorMessage: String) : ValidationResult()
}
