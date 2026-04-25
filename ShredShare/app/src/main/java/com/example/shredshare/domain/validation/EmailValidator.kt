package com.example.shredshare.domain.validation

import android.util.Patterns

class EmailValidator {
    fun validate(email: String): ValidationResult {
        if (email.isBlank()) {
            return ValidationResult.Error("Имейлът е задължителен")
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return ValidationResult.Error("Невалиден формат на имейла")
        }
        return ValidationResult.Success
    }
}
