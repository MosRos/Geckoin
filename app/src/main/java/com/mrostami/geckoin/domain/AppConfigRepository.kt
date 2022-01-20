package com.mrostami.geckoin.domain

import kotlinx.coroutines.flow.Flow

interface AppConfigRepository {
    fun changeTheme(mode: Int)
    fun getThemeMode() : Flow<Int>
}