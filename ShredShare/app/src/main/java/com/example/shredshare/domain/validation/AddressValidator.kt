package com.example.shredshare.domain.validation

class AddressValidator {
    fun validate(address: String): ValidationResult {
        if (address.isBlank()) {
            return ValidationResult.Error("Адресът е задължителен")
        }
        return ValidationResult.Success
    }
}
