package com.mrostami.geckoin.data.repositories

import com.haroldadmin.cnradapter.NetworkResponse
import com.mrostami.geckoin.data.remote.RemoteDataSource
import com.mrostami.geckoin.data.remote.responses.CoinGeckoApiError
import com.mrostami.geckoin.domain.CoinDetailsRepository
import com.mrostami.geckoin.domain.base.Result
import com.mrostami.geckoin.model.CoinDetailResponse
import com.mrostami.geckoin.model.CoinDetailsInfo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CoinDetailsRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : CoinDetailsRepository {
    override fun getCoinDetails(coinId: String): Flow<Result<CoinDetailsInfo>> {

        return object : RemoteRepositoryAdapter<String, CoinDetailResponse, CoinDetailsInfo>() {

            override suspend fun getFromApi(parameters: String): NetworkResponse<CoinDetailResponse, CoinGeckoApiError> {
                return remoteDataSource.getCoinDetailsInfo(coinId = parameters)
            }

            override suspend fun mapApiResponse(response: CoinDetailResponse): CoinDetailsInfo? {
                return try {
                    CoinDetailsInfo(
                        id = response.id,
                        symbol = response.symbol,
                        name = response.name,
                        marketCapRank = response.marketCapRank,
                        imageUrl = response.image?.large,
                        description = response.description?.en,
                        hashingAlgorithm = response.hashingAlgorithm,
                        lastUpdated = response.lastUpdated,
                        sentimentVotesDownPercentage = response.sentimentVotesDownPercentage,
                        sentimentVotesUpPercentage = response.sentimentVotesUpPercentage,
                        categories = response.categories,
                        coingeckoRank = response.coingeckoRank,
                        coingeckoScore = response.coingeckoScore,
                        communityScore = response.communityScore,
                        developerScore = response.developerScore
                    )
                } catch (e: Exception) {
                    null
                }
            }
        }.invoke(parameters = coinId)
    }
}