package com.example.shredshare.domain.validation

class PasswordValidator {
    fun validate(password: String): ValidationResult {
        if (password.isBlank()) {
            return ValidationResult.Error("Паролата е задължителна")
        }
        if (password.length < 8) {
            return ValidationResult.Error("Паролата трябва да е поне 8 символа")
        }
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasSymbol = password.any { !it.isLetterOrDigit() }
        
        if (!hasUpperCase || !hasLowerCase) {
            return ValidationResult.Error("Паролата трябва да съдържа поне една малка и една главна буква")
        }
        if (!hasSymbol) {
            return ValidationResult.Error("Паролата трябва да съдържа поне един специален символ")
        }
        return ValidationResult.Success
    }

    fun validateMatching(password: String, confirmPassword: String): ValidationResult {
        if (confirmPassword.isBlank()) {
            return ValidationResult.Error("Моля, потвърдете паролата")
        }
        if (password != confirmPassword) {
            return ValidationResult.Error("Паролите не съвпадат")
        }
        return ValidationResult.Success
    }
}
