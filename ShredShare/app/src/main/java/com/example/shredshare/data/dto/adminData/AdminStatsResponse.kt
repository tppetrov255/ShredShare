package com.example.shredshare.data.dto.adminData

data class AdminStatsResponse(
    val totalPending: Int,
    val totalApproved: Int,
    val totalOwners: Int,
    val totalUsers: Int
)