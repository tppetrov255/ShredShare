package com.example.shredshare.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shredshare.data.repository.EquipmentRepository
import com.example.shredshare.domain.validation.NameValidator
import com.example.shredshare.domain.validation.NumericValidator
import com.example.shredshare.domain.validation.ValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddEquipmentViewModel : ViewModel() {

    private val repository = EquipmentRepository()
    private val nameValidator = NameValidator()
    private val numericValidator = NumericValidator()

    data class AddEquipmentUiState(
        val equipmentId: Int? = null,
        val name: String = "",
        val type: String = "",
        val brand: String = "",
        val size: String = "",
        val pricePerDay: String = "",
        val quantity: String = "",
        val description: String = "",
        val imageUri: Uri? = null,
        val existingImageUrl: String? = null,
        val isEditMode: Boolean = false,
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false,
        val message: String? = null,
        val validationErrors: Map<String, String> = emptyMap()
    )

    private val _uiState = MutableStateFlow(AddEquipmentUiState())
    val uiState: StateFlow<AddEquipmentUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) {
        _uiState.value = _uiState.value.copy(name = value)
    }

    fun onTypeChange(value: String) {
        _uiState.value = _uiState.value.copy(type = value)
    }

    fun onBrandChange(value: String) {
        _uiState.value = _uiState.value.copy(brand = value)
    }

    fun onSizeChange(value: String) {
        _uiState.value = _uiState.value.copy(size = value)
    }

    fun onPricePerDayChange(value: String) {
        _uiState.value = _uiState.value.copy(pricePerDay = value)
    }

    fun onQuantityChange(value: String) {
        _uiState.value = _uiState.value.copy(quantity = value)
    }

    fun onDescriptionChange(value: String) {
        _uiState.value = _uiState.value.copy(description = value)
    }

    fun onImageSelected(uri: Uri?) {
        _uiState.value = _uiState.value.copy(imageUri = uri)
    }

    private fun validate(): Boolean {
        val state = _uiState.value
        val errors = mutableMapOf<String, String>()

        val nameRes = nameValidator.validate(state.name, "Името")
        if (nameRes is ValidationResult.Error) errors["name"] = nameRes.errorMessage

        val typeRes = nameValidator.validate(state.type, "Типът")
        if (typeRes is ValidationResult.Error) errors["type"] = typeRes.errorMessage

        val brandRes = nameValidator.validate(state.brand, "Марката")
        if (brandRes is ValidationResult.Error) errors["brand"] = brandRes.errorMessage

        val sizeRes = nameValidator.validate(state.size, "Размерът")
        if (sizeRes is ValidationResult.Error) errors["size"] = sizeRes.errorMessage

        val priceRes = numericValidator.validateDecimal(state.pricePerDay, "Цената на ден")
        if (priceRes is ValidationResult.Error) errors["pricePerDay"] = priceRes.errorMessage

        val qtyRes = numericValidator.validateInt(state.quantity, "Количеството")
        if (qtyRes is ValidationResult.Error) errors["quantity"] = qtyRes.errorMessage

        val descRes = nameValidator.validate(state.description, "Описанието")
        if (descRes is ValidationResult.Error) errors["description"] = descRes.errorMessage

        _uiState.value = _uiState.value.copy(validationErrors = errors)
        return errors.isEmpty()
    }

    fun createEquipment(
        context: Context,
        wardrobeId: Int
    ) {
        if (!validate()) return

        val state = _uiState.value

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                isSuccess = false,
                message = null
            )

            val result = repository.createEquipment(
                context = context,
                wardrobeId = wardrobeId,
                name = state.name.trim(),
                type = state.type.trim(),
                brand = state.brand.trim(),
                size = state.size.trim(),
                pricePerDay = state.pricePerDay.trim(),
                quantity = state.quantity.trim(),
                description = state.description.trim(),
                imageUri = state.imageUri
            )

            if (result?.success == true) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = true,
                    message = result.message
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = false,
                    message = result?.message ?: "Неуспешно добавяне на артикул"
                )
            }
        }
    }

    fun loadEquipmentForEdit(equipmentId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, message = null)

            val item = repository.getEquipmentById(equipmentId)

            if (item != null) {
                _uiState.value = _uiState.value.copy(
                    equipmentId = item.equipmentId,
                    name = item.name,
                    type = item.type ?: "",
                    brand = item.brand ?: "",
                    size = item.size ?: "",
                    pricePerDay = item.pricePerDay?.toString() ?: "",
                    quantity = item.quantity?.toString() ?: "",
                    description = item.description ?: "",
                    existingImageUrl = item.imageUrl,
                    isEditMode = true,
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "Неуспешно зареждане на артикула"
                )
            }
        }
    }

    fun updateEquipment(
        context: Context,
        equipmentId: Int
    ) {
        if (!validate()) return

        val state = _uiState.value

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                isSuccess = false,
                message = null
            )

            val result = repository.updateEquipment(
                context = context,
                equipmentId = equipmentId,
                name = state.name.trim(),
                type = state.type.trim(),
                brand = state.brand.trim(),
                size = state.size.trim(),
                pricePerDay = state.pricePerDay.trim(),
                quantity = state.quantity.trim(),
                description = state.description.trim(),
                imageUri = state.imageUri
            )

            if (result?.success == true) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = true,
                    message = result.message
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = false,
                    message = result?.message ?: "Неуспешна редакция на артикул"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = AddEquipmentUiState()
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}
