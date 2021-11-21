package com.mrostami.geckoin.data.repositories

import com.haroldadmin.cnradapter.NetworkResponse
import com.mrostami.geckoin.domain.base.Result
import com.mrostami.geckoin.presentation.utils.NetworkUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber

/**
 * Represents a resource which needs to be loaded from the network and persisted to the Database
 *
 * @param P the input param for network request
 * @param T The type of the entity to be fetched/stored in the database
 * @param U The success type of [NetworkResponse]
 * @param V The error type of [NetworkResponse]
 *
 * The items emitted from this class are wrapped in a [Resource] class. The items are emitted in a [Flow].
 * Following is sequence of actions taken:
 * 1. Emit Resource.Loading
 * 2. Query database for cached data using [getFromDatabase].
 * 3. Emit Cached data from the database, if there is any. The decision to emit data is made using the abstract
 * [validateCache] method.
 * 4. Fetch data from the API using [getFromApi]
 * 5. If the fetch is successful, persist the data using [persistData] else emit [Resource.Error] with the cached data and terminate
 * 6. Emit [Resource.Success] with the newly persisted data if the fetch was successful
 *
 * The class also contains two properties [offset] and [limit] so that they can be dynamically updated. They are
 * passed to [getFromDatabase] on every invocation. They can be updated using the [updateParams] method.
 */

abstract class RepositoryResource<in P : Any?, T : Any?, U : Any, V : Any>(
    private val forceRefresh: Boolean = false,
    private val rateLimiter: Long = 5000,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    abstract suspend fun getFromDatabase(): T?
    abstract suspend fun validateCache(cachedData: T?): Boolean
    abstract fun shouldFetchFromApi() : Boolean
    abstract suspend fun getFromApi(): NetworkResponse<U, V>
    abstract suspend fun persistData(apiData: U)

    operator fun invoke(parameters: P) : Flow<Result<T>> = execute(parameters)
        .catch { e -> emit(Result.Error(Exception(e))) }
        .flowOn(coroutineDispatcher)

    open fun execute(parameters: P) : Flow<Result<T>> = flow {
        val cachedData = getFromDatabase()

        if (validateCache(cachedData)) {
            emit(Result.Success(cachedData!!))
        } else {
            emit(Result.Empty)
        }

        if (NetworkUtils.isConnected()) {
            if (shouldFetchFromApi()) {

                emit(Result.Loading)

                val apiResponse = getFromApi()
                when (apiResponse) {
                    is NetworkResponse.Success -> {
                        persistData(apiResponse.body)
                        val refreshedData = getFromDatabase()
                        if (validateCache(refreshedData)) {
                            emit(Result.Success(refreshedData!!))
                        } else {
                            emit(Result.Error(Exception("Oops!"), message = "Failed to load cached data"))
                        }
                    }
                    is NetworkResponse.ServerError -> {
                        emit(Result.Error(apiResponse.error))
                    }
                    is NetworkResponse.NetworkError -> {
                        val error = apiResponse.error
                        emit(Result.Error(error))
                    }
                }
            }
        } else {
            kotlinx.coroutines.delay(350)
            emit(Result.Error(Exception("No internet connection"), message = "No internet connection"))
        }
    }
}