package com.example.shredshare.domain.validation

class NameValidator {
    fun validate(name: String, fieldName: String = "Името"): ValidationResult {
        if (name.isBlank()) {
            return ValidationResult.Error("$fieldName е задължително")
        }
        return ValidationResult.Success
    }
}
