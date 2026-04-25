package com.example.shredshare.domain.validation

import java.math.BigDecimal

class NumericValidator {
    fun validateDecimal(value: String, fieldName: String, allowNegative: Boolean = false): ValidationResult {
        if (value.isBlank()) {
            return ValidationResult.Error("$fieldName е задължително")
        }
        val numericValue = value.toBigDecimalOrNull()
        if (numericValue == null) {
            return ValidationResult.Error("Невалидна стойност за $fieldName")
        }
        if (!allowNegative && numericValue < BigDecimal.ZERO) {
            return ValidationResult.Error("$fieldName не може да е отрицателна стойност")
        }
        return ValidationResult.Success
    }

    fun validateInt(value: String, fieldName: String, allowNegative: Boolean = false): ValidationResult {
        if (value.isBlank()) {
            return ValidationResult.Error("$fieldName е задължително")
        }
        val numericValue = value.toIntOrNull()
        if (numericValue == null) {
            return ValidationResult.Error("Невалидна стойност за $fieldName")
        }
        if (!allowNegative && numericValue < 0) {
            return ValidationResult.Error("$fieldName не може да е отрицателна стойност")
        }
        return ValidationResult.Success
    }
}
