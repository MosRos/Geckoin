package com.mrostami.geckoin.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.haroldadmin.cnradapter.NetworkResponse
import com.mrostami.geckoin.data.local.LocalDataSource
import com.mrostami.geckoin.data.remote.RemoteDataSource
import com.mrostami.geckoin.domain.AllCoinsRepository
import com.mrostami.geckoin.domain.base.Result
import com.mrostami.geckoin.model.Coin
import com.mrostami.geckoin.presentation.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AllCoinsRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) : AllCoinsRepository {

    companion object {
        const val COINS_PAGE_SIZE = 50
    }

    private val syncRateLimiter = 7 * 24 * 60 * 60 * 1000L
    private val pageConfig = PagingConfig(
        pageSize = COINS_PAGE_SIZE,
        prefetchDistance = 5,
        enablePlaceholders = true,
        initialLoadSize = 100,
        maxSize = 200
    )
    private val coinsPagingSourceFactory = { localDataSource.getPagedCoins() }
    private fun getSearchPagingSourceFactory(query: String): () -> PagingSource<Int, Coin> =
        { localDataSource.searchPagedCoins(input = query) }


    override fun syncAllCoins(forceRefresh: Boolean): Flow<Result<Boolean>> {
        return flow {
            if (NetworkUtils.isConnected()) {
                if (isNotLimited() || forceRefresh) {
                    try {
                        val result = remoteDataSource.getAllCoins()
                        when (result) {
                            is NetworkResponse.Success -> {
                                saveCoins(result.body)
                            }
                            is NetworkResponse.ServerError -> {
                                emit(Result.Error(result.error))
                            }
                            is NetworkResponse.NetworkError -> {
                                emit(Result.Error(result.error))
                            }
                            is NetworkResponse.UnknownError -> {
                                emit(Result.Error(Exception(result.error)))
                            }
                        }
                    } catch (e: Exception) {
                        emit(Result.Error(e))
                    }
                } else {
                    emit(Result.Success(true))
                }

            } else {
                emit(Result.Error(Exception("No internet connection")))
            }
        }
    }

    override fun searchCoins(searchInput: String?): Flow<PagingData<Coin>> {
        val pagingSourceFactory =
            if (searchInput.isNullOrEmpty())
                coinsPagingSourceFactory
            else
                getSearchPagingSourceFactory(searchInput)

        return Pager(
            config = pageConfig,
            initialKey = 1,
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    private fun isNotLimited(): Boolean {
        val lastSyncDate: Long = localDataSource.getLastSyncDate()
        return System.currentTimeMillis() - lastSyncDate > syncRateLimiter
    }

    suspend fun saveCoins(coins: List<Coin>) {
        withContext(Dispatchers.IO) {
            localDataSource.insertCoins(coins)
        }
        localDataSource.setLastSyncDate(System.currentTimeMillis())
    }
}