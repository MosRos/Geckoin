package com.mrostami.geckoin.data.preferences

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

class PreferencesHelper @Inject constructor(private val preferences: SharedPreferences) {

    private val SELECTED_THEME_MODE = Pair("theme-mode", 0)
    var selectedThemeMode: Int
        get() = preferences.getInt(SELECTED_THEME_MODE.first, SELECTED_THEME_MODE.second)
        set(value) = preferences.edit {
            putInt(SELECTED_THEME_MODE.first, value)
        }

    private val AUTH_TOKEN_PAIR = Pair("auth_token", null)
    var authToken: String?
        get() = preferences.getString(AUTH_TOKEN_PAIR.first, AUTH_TOKEN_PAIR.second)
        set(value) = preferences.edit {
            putString(AUTH_TOKEN_PAIR.first, value)
        }
}