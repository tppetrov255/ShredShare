package com.example.shredshare.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.shredshare.data.remote.RetrofitInstance
import com.example.shredshare.data.dto.adminData.SimpleResponse
import com.example.shredshare.data.dto.equipment.EquipmentResponseDto
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class EquipmentRepository {

    private val gson = Gson()

    suspend fun createEquipment(
        context: Context,
        wardrobeId: Int,
        name: String,
        type: String,
        brand: String,
        size: String,
        pricePerDay: String,
        quantity: String,
        description: String,
        imageUri: Uri?
    ): SimpleResponse? {
        return try {
            val wardrobeIdBody = wardrobeId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val typeBody = type.toRequestBody("text/plain".toMediaTypeOrNull())
            val brandBody = brand.toRequestBody("text/plain".toMediaTypeOrNull())
            val sizeBody = size.toRequestBody("text/plain".toMediaTypeOrNull())
            val priceBody = pricePerDay.toRequestBody("text/plain".toMediaTypeOrNull())
            val quantityBody = quantity.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionBody = description.toRequestBody("text/plain".toMediaTypeOrNull())

            val imagePart = imageUri?.let { uri ->
                val file = getFileFromUri(context, uri)
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("image", file.name, requestFile)
            }

            val response = RetrofitInstance.equipmentApi.createEquipment(
                wardrobeId = wardrobeIdBody,
                name = nameBody,
                type = typeBody,
                brand = brandBody,
                size = sizeBody,
                pricePerDay = priceBody,
                quantity = quantityBody,
                description = descriptionBody,
                image = imagePart
            )

            if (response.isSuccessful) {
                response.body()
            } else {
                parseError(response.errorBody()?.string())
            }
        } catch (e: Exception) {
            Log.e("EquipmentRepository", "Create equipment failed", e)
            null
        }
    }

    private fun getFileFromUri(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "upload_equipment_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { output ->
            inputStream?.copyTo(output)
        }
        return file
    }

    private fun parseError(errorJson: String?): SimpleResponse? {
        return try {
            gson.fromJson(errorJson, SimpleResponse::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getEquipmentByWardrobe(wardrobeId: Int): List<EquipmentResponseDto>? {
        return try {
            val response = RetrofitInstance.equipmentApi.getEquipmentByWardrobe(wardrobeId)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getEquipmentById(equipmentId: Int): EquipmentResponseDto? {
        return try {
            val response = RetrofitInstance.equipmentApi.getEquipmentById(equipmentId)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateEquipment(
        context: Context,
        equipmentId: Int,
        name: String,
        type: String,
        brand: String,
        size: String,
        pricePerDay: String,
        quantity: String,
        description: String,
        imageUri: Uri?
    ): SimpleResponse? {
        return try {
            val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val typeBody = type.toRequestBody("text/plain".toMediaTypeOrNull())
            val brandBody = brand.toRequestBody("text/plain".toMediaTypeOrNull())
            val sizeBody = size.toRequestBody("text/plain".toMediaTypeOrNull())
            val priceBody = pricePerDay.toRequestBody("text/plain".toMediaTypeOrNull())
            val quantityBody = quantity.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionBody = description.toRequestBody("text/plain".toMediaTypeOrNull())

            val imagePart = imageUri?.let { uri ->
                val file = getFileFromUri(context, uri)
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("image", file.name, requestFile)
            }

            val response = RetrofitInstance.equipmentApi.updateEquipment(
                equipmentId = equipmentId,
                name = nameBody,
                type = typeBody,
                brand = brandBody,
                size = sizeBody,
                pricePerDay = priceBody,
                quantity = quantityBody,
                description = descriptionBody,
                image = imagePart
            )

            if (response.isSuccessful) {
                response.body()
            } else {
                parseError(response.errorBody()?.string())
            }
        } catch (e: Exception) {
            Log.e("EquipmentRepository", "Update equipment failed", e)
            null
        }
    }

    suspend fun deleteEquipment(equipmentId: Int): SimpleResponse? {
        return try {
            val response = RetrofitInstance.equipmentApi.deleteEquipment(equipmentId)
            if (response.isSuccessful) {
                response.body()
            } else {
                parseError(response.errorBody()?.string())
            }
        } catch (e: Exception) {
            Log.e("EquipmentRepository", "Delete equipment failed", e)
            null
        }
    }
}