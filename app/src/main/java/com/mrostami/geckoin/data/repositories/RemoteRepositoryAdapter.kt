package com.mrostami.geckoin.data.repositories

import com.haroldadmin.cnradapter.NetworkResponse
import com.mrostami.geckoin.data.remote.responses.CoinGeckoApiError
import com.mrostami.geckoin.domain.base.Result
import com.mrostami.geckoin.presentation.utils.NetworkUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


/**
 * Represents a resource which needs to be loaded from the network and persisted to the Database
 *
 * @param P the input param for network request
 * @param T The api success response
 * @param R is the expected output
 *
 */

abstract class RemoteRepositoryAdapter<in P : Any?, T : Any, out R : Any>(
    val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    abstract suspend fun getFromApi(parameters: P): NetworkResponse<T, CoinGeckoApiError>
    abstract suspend fun mapApiResponse(response: T) : R?

    operator fun invoke(parameters: P): Flow<Result<R>> = flow {

        emit(Result.Loading)

        if (NetworkUtils.isConnected()) {
            when (val response = getFromApi(parameters)) {
                is NetworkResponse.Success -> {
                    val data: R? = mapApiResponse(response.body)
                    if (data != null) {
                        emit(Result.Success(data))
                    } else {
                        emit(
                            Result.Error(
                                Exception("Oops!"),
                                message = "Failed to load data"
                            )
                        )
                    }
                }
                is NetworkResponse.ServerError -> {
                    emit(Result.Error(response.error))
                }
                is NetworkResponse.NetworkError -> {
                    val error = response.error
                    emit(Result.Error(error))
                }

                is NetworkResponse.UnknownError -> {
                    emit(Result.Error(Exception("Unknown error occurred!")))
                }
            }
        } else {
            delay(200)
            emit(
                Result.Error(
                    Exception("No internet connection"),
                    message = "No internet connection"
                )
            )
        }
    }.catch { e ->
        emit(Result.Error(Exception(e)))
    }.flowOn(coroutineDispatcher)

}