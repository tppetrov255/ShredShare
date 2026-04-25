package com.example.shredshare.data.remote

import com.example.shredshare.data.dto.booking.BookingResponse
import com.example.shredshare.data.dto.booking.CreateBookingRequest
import com.example.shredshare.data.dto.OwnerBookingResponse
import com.example.shredshare.data.dto.UserBookingResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.PUT
import retrofit2.http.Query
import com.example.shredshare.data.dto.booking.UpdateBookingStatusRequest
interface BookingApi {

    @POST("bookings/create")
    suspend fun createBooking(
        @Body request: CreateBookingRequest
    ): Response<BookingResponse>

    @GET("bookings/user/{userId}")
    suspend fun getBookingsByUser(
        @Path("userId") userId: Int
    ): Response<List<UserBookingResponse>>

    @PUT("bookings/cancel/{bookingId}")
    suspend fun cancelBooking(
        @Path("bookingId") bookingId: Int,
        @Query("userId") userId: Int
    ): Response<BookingResponse>

    @GET("bookings/owner/{ownerId}")
    suspend fun getBookingsByOwner(
        @Path("ownerId") ownerId: Int
    ): Response<List<OwnerBookingResponse>>

    @PUT("bookings/owner/{bookingId}/status")
    suspend fun updateBookingStatusByOwner(
        @Path("bookingId") bookingId: Int,
        @Body request: UpdateBookingStatusRequest
    ): Response<BookingResponse>

}