package com.example.shredshare.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shredshare.data.dto.resort.Resort
import com.example.shredshare.data.remote.RetrofitInstance
import com.example.shredshare.data.repository.ResortRepository
import com.example.shredshare.data.repository.WardrobeRepository
import com.example.shredshare.domain.validation.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateWardrobeViewModel : ViewModel() {

    private val repository = WardrobeRepository()
    private val resortRepository = ResortRepository(RetrofitInstance.resortApi)
    
    private val nameValidator = NameValidator()
    private val addressValidator = AddressValidator()
    private val phoneValidator = PhoneValidator()

    private val _uiState = MutableStateFlow(CreateWardrobeUiState())
    val uiState: StateFlow<CreateWardrobeUiState> = _uiState.asStateFlow()

    data class CreateWardrobeUiState(
        val wardrobeName: String = "",
        val address: String = "",
        val phone: String = "",
        val description: String = "",
        val selectedLocationText: String = "Все още няма избрано местоположение",
        val latitude: Double? = null,
        val longitude: Double? = null,
        val imageUri: Uri? = null,
        val resorts: List<Resort> = emptyList(),
        val selectedResort: Resort? = null,
        val isLoading: Boolean = false,
        val success: Boolean = false,
        val error: String? = null,
        val validationErrors: Map<String, String> = emptyMap()
    )

    init {
        loadResorts()
    }

    private fun loadResorts() {
        viewModelScope.launch {
            val result = resortRepository.getAllResorts()
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(resorts = result.getOrDefault(emptyList()))
            }
        }
    }

    fun onWardrobeNameChange(value: String) {
        _uiState.value = _uiState.value.copy(wardrobeName = value)
    }

    fun onAddressChange(value: String) {
        _uiState.value = _uiState.value.copy(address = value)
    }

    fun onPhoneChange(value: String) {
        _uiState.value = _uiState.value.copy(phone = value)
    }

    fun onDescriptionChange(value: String) {
        _uiState.value = _uiState.value.copy(description = value)
    }

    fun onResortSelected(resort: Resort) {
        _uiState.value = _uiState.value.copy(selectedResort = resort)
    }

    fun onImageSelected(uri: Uri?) {
        _uiState.value = _uiState.value.copy(imageUri = uri)
    }

    fun updateLocation(latitude: Double, longitude: Double, address: String) {
        _uiState.value = _uiState.value.copy(
            latitude = latitude,
            longitude = longitude,
            selectedLocationText = address
        )
    }

    private fun validate(): Boolean {
        val state = _uiState.value
        val errors = mutableMapOf<String, String>()

        val nameRes = nameValidator.validate(state.wardrobeName, "Името на гардероба")
        if (nameRes is ValidationResult.Error) errors["wardrobeName"] = nameRes.errorMessage

        val addressRes = addressValidator.validate(state.address)
        if (addressRes is ValidationResult.Error) errors["address"] = addressRes.errorMessage

        val phoneRes = phoneValidator.validate(state.phone)
        if (phoneRes is ValidationResult.Error) errors["phone"] = phoneRes.errorMessage

        if (state.selectedResort == null) {
            errors["resort"] = "Моля, изберете курорт"
        }
        
        if (state.latitude == null) {
            errors["location"] = "Моля, изберете локация на картата"
        }

        _uiState.value = _uiState.value.copy(validationErrors = errors)
        return errors.isEmpty()
    }

    fun createWardrobe(context: Context, ownerId: Int) {
        if (!validate()) return

        val state = _uiState.value
        if (ownerId <= 0) {
            _uiState.value = _uiState.value.copy(error = "Грешка: Невалидно потребителско ID ($ownerId)")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)

            val result = repository.createWardrobe(
                context = context,
                ownerId = ownerId,
                resortId = state.selectedResort?.resortId ?: 0,
                name = state.wardrobeName.trim(),
                address = state.address.trim(),
                phone = state.phone.trim(),
                description = state.description.trim(),
                latitude = state.latitude,
                longitude = state.longitude,
                imageUri = state.imageUri
            )

            if (result?.success == true) {
                _uiState.value = _uiState.value.copy(isLoading = false, success = true)
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result?.message ?: "Неуспешно създаване"
                )
            }
        }
    }
}
