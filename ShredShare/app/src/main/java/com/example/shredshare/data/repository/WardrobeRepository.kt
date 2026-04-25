package com.example.shredshare.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.shredshare.data.dto.wardrobe.WardrobeResponse
import com.example.shredshare.data.remote.RetrofitInstance
import com.example.shredshare.data.dto.equipment.WardrobeListEquipmentDto
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class WardrobeRepository {

    private val gson = Gson()

    suspend fun createWardrobe(
        context: Context,
        ownerId: Int,
        resortId: Int,
        name: String,
        address: String,
        phone: String,
        description: String,
        latitude: Double?,
        longitude: Double?,
        imageUri: Uri?
    ): WardrobeResponse? {
        return try {
            val ownerIdBody = ownerId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val resortIdBody = resortId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val addressBody = address.toRequestBody("text/plain".toMediaTypeOrNull())
            val phoneBody = phone.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
            val latitudeBody = latitude!!.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val longitudeBody = longitude!!.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            val imagePart = imageUri?.let { uri ->
                val file = getFileFromUri(context, uri)
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("imageUrl", file.name, requestFile)
            }

            val response = RetrofitInstance.wardrobeApi.createWardrobe(
                ownerIdBody, resortIdBody, nameBody, addressBody, phoneBody, descriptionBody,
                latitudeBody, longitudeBody, imagePart
            )

            if (response.isSuccessful) {
                response.body()
            } else {
                parseError(response.errorBody()?.string())
            }
        } catch (e: Exception) {
            Log.e("WardrobeRepository", "Create wardrobe failed", e)
            null
        }
    }

    private fun getFileFromUri(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "upload_image_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { output ->
            inputStream?.copyTo(output)
        }
        return file
    }

    private fun parseError(errorJson: String?): WardrobeResponse? {
        return try {
            gson.fromJson(errorJson, WardrobeResponse::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getApprovedWardrobesByResort(resortId: Int): List<WardrobeListEquipmentDto>? {
        return try {
            val response = RetrofitInstance.wardrobeApi.getApprovedWardrobesByResort(resortId)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("WardrobeRepository", "Load wardrobes by resort failed", e)
            null
        }
    }
}