package com.example.shredshare.data.repository

import com.example.shredshare.data.remote.RetrofitInstance
import com.example.shredshare.data.dto.booking.BookingResponse
import com.example.shredshare.data.dto.booking.CreateBookingRequest
import com.example.shredshare.data.dto.OwnerBookingResponse
import com.example.shredshare.data.dto.UserBookingResponse
import com.google.gson.Gson


class BookingRepository {

    private val gson = Gson()

    suspend fun createBooking(request: CreateBookingRequest): BookingResponse? {
        return try {
            val response = RetrofitInstance.bookingApi.createBooking(request)
            if (response.isSuccessful) {
                response.body()
            } else {
                gson.fromJson(response.errorBody()?.string(), BookingResponse::class.java)
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getBookingsByUser(userId: Int): List<UserBookingResponse>? {
        return try {
            val response = RetrofitInstance.bookingApi.getBookingsByUser(userId)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun cancelBooking(bookingId: Int, userId: Int): BookingResponse? {
        return try {
            val response = RetrofitInstance.bookingApi.cancelBooking(bookingId, userId)
            if (response.isSuccessful) {
                response.body()
            } else {
                gson.fromJson(response.errorBody()?.string(), BookingResponse::class.java)
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getBookingsByOwner(ownerId: Int): List<OwnerBookingResponse>? {
        return try {
            val response = RetrofitInstance.bookingApi.getBookingsByOwner(ownerId)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateBookingStatusByOwner(
        bookingId: Int,
        ownerId: Int,
        newStatus: String
    ): BookingResponse? {
        return try {
            val response = RetrofitInstance.bookingApi.updateBookingStatusByOwner(
                bookingId,
                com.example.shredshare.data.dto.booking.UpdateBookingStatusRequest(
                    ownerId = ownerId,
                    newStatus = newStatus
                )
            )

            if (response.isSuccessful) {
                response.body()
            } else {
                gson.fromJson(response.errorBody()?.string(), BookingResponse::class.java)
            }
        } catch (e: Exception) {
            null
        }
    }

}