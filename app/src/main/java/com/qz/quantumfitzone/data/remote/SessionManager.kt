package com.qz.quantumfitzone.data.remote

import android.content.Context

class SessionManager(context: Context) {
    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveSession(
        user: String,
        password: String,
        accessToken: String,
        refreshToken: String
    ) {
        preferences.edit()
            .putString(KEY_USER, user)
            .putString(KEY_PASSWORD, password)
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .apply()
    }

    fun getUser(): String = preferences.getString(KEY_USER, "").orEmpty()

    fun getPassword(): String = preferences.getString(KEY_PASSWORD, "").orEmpty()

    fun getAccessToken(): String = preferences.getString(KEY_ACCESS_TOKEN, "").orEmpty()

    fun getRefreshToken(): String = preferences.getString(KEY_REFRESH_TOKEN, "").orEmpty()

    fun clearSession() {
        preferences.edit().clear().apply()
    }

    private companion object {
        const val PREFS_NAME = "credenciales"
        const val KEY_USER = "user"
        const val KEY_PASSWORD = "pass"
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
    }
}
