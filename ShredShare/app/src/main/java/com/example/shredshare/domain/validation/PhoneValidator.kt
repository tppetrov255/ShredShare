package com.example.shredshare.domain.validation

class PhoneValidator {
    fun validate(phone: String): ValidationResult {
        if (phone.isBlank()) {
            return ValidationResult.Error("Телефонният номер е задължителен")
        }
        // Basic phone validation logic if needed, currently it checks if it's just digits or +
        if (!phone.all { it.isDigit() || it == '+' || it == ' ' }) {
            return ValidationResult.Error("Невалиден формат на телефона")
        }
        return ValidationResult.Success
    }
}
