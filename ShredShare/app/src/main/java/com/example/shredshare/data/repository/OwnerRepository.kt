package com.example.shredshare.data.repository

import com.example.shredshare.data.remote.RetrofitInstance
import com.example.shredshare.ui.theme.screens.OwnerWardrobeUi
class OwnerRepository {

    suspend fun getWardrobesByOwner(ownerId: Int): List<OwnerWardrobeUi>? {
        return try {
            val response = RetrofitInstance.ownerApi.getWardrobesByOwner(ownerId)

            if (response.isSuccessful) {
                response.body()?.map {
                    OwnerWardrobeUi(
                        wardrobeId = it.wardrobeId,
                        name = it.name,
                        resortName = it.resortName,
                        address = it.address ?: "",
                        phone = it.phone ?: "",
                        description = it.description ?: "",
                        imageUrl = it.imageUrl,
                        status = it.status
                    )
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}