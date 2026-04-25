package com.example.shredshare.data.remote

import com.example.shredshare.data.dto.resort.Resort
import retrofit2.Response
import retrofit2.http.GET

interface ResortApi {

    @GET("resorts")
    suspend fun getAllResorts(): Response<List<Resort>>
}