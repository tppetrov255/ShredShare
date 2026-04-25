package com.example.shredshare.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("shredshare_session", Context.MODE_PRIVATE)

    fun saveLogin(userId: Int, role: String) {
        prefs.edit()
            .putBoolean("is_logged_in", true)
            .putInt("user_id", userId)
            .putString("role", role)
            .apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("is_logged_in", false)
    }

    fun getUserId(): Int {
        return prefs.getInt("user_id", -1)
    }

    fun getRole(): String? {
        return prefs.getString("role", null)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}