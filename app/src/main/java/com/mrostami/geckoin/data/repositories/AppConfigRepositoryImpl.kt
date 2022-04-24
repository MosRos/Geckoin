package com.mrostami.geckoin.data.repositories

import com.mrostami.geckoin.data.local.LocalDataSource
import com.mrostami.geckoin.domain.AppConfigRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class AppConfigRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource
) : AppConfigRepository {

    override suspend fun changeTheme(mode: Int) {
        localDataSource.changeTheme(mode)
    }

    override suspend fun getThemeMode(): Flow<Int> {
        return flow {
            emitAll(localDataSource.getThemeMode())
        }
    }
}