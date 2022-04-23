package com.mrostami.geckoin.data.repositories

import com.haroldadmin.cnradapter.NetworkResponse
import com.mrostami.geckoin.data.remote.RemoteDataSource
import com.mrostami.geckoin.data.remote.responses.CoinGeckoApiError
import com.mrostami.geckoin.data.remote.responses.PriceChartResponse
import com.mrostami.geckoin.domain.PriceHistoryRepository
import com.mrostami.geckoin.domain.base.Result
import com.mrostami.geckoin.model.PriceEntry
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PriceHistoryRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : PriceHistoryRepository {
    override fun getPriceHistory(
        coinId: String
    ): Flow<Result<List<PriceEntry>>> {

        val result = object : RemoteRepositoryAdapter<String, PriceChartResponse, List<PriceEntry>>() {

            override suspend fun getFromApi(parameters: String): NetworkResponse<PriceChartResponse, CoinGeckoApiError> {
                return remoteDataSource.getMarketChartInfo(coinId = parameters)
            }

            override suspend fun mapApiResponse(response: PriceChartResponse): List<PriceEntry>? {
                return response.prices?.map { it ->
                    PriceEntry(
                        coinId = coinId,
                        timeStamp = it[0].toLong(),
                        price = it[1]
                    )
                } ?: listOf()
            }
        }

        return result.invoke(parameters = coinId)

//        return flow {
//            emit(Result.Loading)
//
//            if (NetworkUtils.isConnected()) {
//                val response = remoteDataSource.getMarketChartInfo(coinId = coinId)
//                when (response) {
//                    is NetworkResponse.Success -> {
//                        val data = mapApiResponse(coinId = coinId, priceChartResponse = response.body)
//                        emit(Result.Success(data))
//                    }
//                    is NetworkResponse.ServerError -> {
//                        emit(Result.Error(response.error))
//                    }
//                    is NetworkResponse.NetworkError -> {
//                        val error = response.error
//                        emit(Result.Error(error))
//                    }
//                }
//            } else {
//                delay(200L)
//                emit(Result.Error(Exception("No internet connection"), message = "No internet connection"))
//            }
//        }
    }

//    private fun mapApiResponse(coinId: String, priceChartResponse: PriceChartResponse) : List<PriceEntry> {
//            return priceChartResponse.prices?.map { it ->
//                PriceEntry(
//                    coinId = coinId,
//                    timeStamp = it[0].toLong(),
//                    price = it[1]
//                )
//            } ?: listOf()
//    }
}