package com.example.shredshare.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val resortApi: ResortApi by lazy {
        retrofit.create(ResortApi::class.java)
    }

    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    val wardrobeApi: WardrobeApi by lazy {
        retrofit.create(WardrobeApi::class.java)
    }

    val adminApi: AdminApi by lazy {
        retrofit.create(AdminApi::class.java)
    }
    val ownerApi: OwnerApi by lazy {
        retrofit.create(OwnerApi::class.java)
    }
    val equipmentApi: EquipmentApi by lazy {
        retrofit.create(EquipmentApi::class.java)
    }

    val userApi: UserApi by lazy {
        retrofit.create(UserApi::class.java)
    }

    val bookingApi: BookingApi by lazy {
        retrofit.create(BookingApi::class.java)
    }
}