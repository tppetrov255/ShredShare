package com.example.shredshare.navigation

import androidx.navigation.NavHostController
import com.example.shredshare.data.local.SessionManager

fun logoutUser(
    navController: NavHostController,
    sessionManager: SessionManager
) {
    sessionManager.clearSession()
    navController.navigate(Screen.Login.route) {
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
    }
}