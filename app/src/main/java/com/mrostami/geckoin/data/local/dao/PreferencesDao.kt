package com.mrostami.geckoin.data.local.dao

interface PreferencesDao {

    fun setSelectedTheme(mode: Int)
    fun getSelectedTheme() : Int

    fun setAuthToken(token: String)
    fun getAuthToken() : String?

    fun setLastSyncDate(time: Long)
    fun getLastSyncDate() : Long
}