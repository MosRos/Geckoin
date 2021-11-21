package com.mrostami.geckoin.data.repositories

import com.haroldadmin.cnradapter.NetworkResponse
import com.mrostami.geckoin.data.local.LocalDataSource
import com.mrostami.geckoin.data.remote.RemoteDataSource
import com.mrostami.geckoin.data.remote.responses.CoinGeckoApiError
import com.mrostami.geckoin.data.remote.responses.TrendCoinsResponse
import com.mrostami.geckoin.domain.GlobalInfoRepository
import com.mrostami.geckoin.domain.base.Result
import com.mrostami.geckoin.model.GlobalMarketInfo
import com.mrostami.geckoin.model.MarketCapPercentageItem
import com.mrostami.geckoin.model.TrendCoin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

class GlobalInfoRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) : GlobalInfoRepository {
    var lastRequestTime2 = 0L
    var lastRequestTime3 = 0L
    override fun getMarketInfo(forceRefresh: Boolean): Flow<Result<GlobalMarketInfo>> {
        val result =
            object : RepositoryResource<Any, GlobalMarketInfo, ResponseBody, CoinGeckoApiError>(
                rateLimiter = 30 * 60 * 1000L,
                forceRefresh = forceRefresh
            ) {
                override suspend fun getFromDatabase(): GlobalMarketInfo? {
                    return try {
                        localDataSource.getGlobalMarketInfo(id = 1)
                    } catch (e: Exception) {
                        Timber.e(e)
                        null
                    }
                }

                override suspend fun validateCache(cachedData: GlobalMarketInfo?): Boolean {
                    return cachedData != null
                }

                override fun shouldFetchFromApi(): Boolean {
                    val trigger: Long = System.currentTimeMillis() - lastRequestTime2
                    Timber.e("Triggered  Time is: $trigger  /n and LastRequeTime: $lastRequestTime2")
                    return forceRefresh || trigger > 10*1000
                }

                override suspend fun getFromApi(): NetworkResponse<ResponseBody, CoinGeckoApiError> {
                    lastRequestTime2 = System.currentTimeMillis()
                    return remoteDataSource.getGlobalMarketInfo()
                }

                override suspend fun persistData(apiData: ResponseBody) {
                    saveGlobalInfo(apiData)
                }
            }

        return result.invoke(Any())
    }

    override fun getTrendingCoins(forceRefresh: Boolean): Flow<Result<List<TrendCoin>>> {
        val result = object :
            RepositoryResource<Any, List<TrendCoin>, TrendCoinsResponse, CoinGeckoApiError>(
                rateLimiter = 30 * 60 * 1000L,
                forceRefresh = forceRefresh
            ) {
            override suspend fun getFromDatabase(): List<TrendCoin>? {
                return localDataSource.getTrendCoins()
            }

            override suspend fun validateCache(cachedData: List<TrendCoin>?): Boolean {
                return !cachedData.isNullOrEmpty()
            }

            override fun shouldFetchFromApi(): Boolean {
                val trigger: Long = System.currentTimeMillis() - lastRequestTime3
                Timber.e("Triggered  Time is: $trigger  /n and LastRequeTime: $lastRequestTime3")
                return forceRefresh || trigger > 5000
            }

            override suspend fun getFromApi(): NetworkResponse<TrendCoinsResponse, CoinGeckoApiError> {
                lastRequestTime3 = System.currentTimeMillis()
                return remoteDataSource.getTrendingCoins()
            }

            override suspend fun persistData(apiData: TrendCoinsResponse) {
                val coins: List<TrendCoin>? = apiData.coinItems?.mapNotNull { it.item }
                if (!coins.isNullOrEmpty()) {
                    withContext(Dispatchers.IO) {
                        localDataSource.insertTrendCoins(coins)
                    }
                }
            }
        }

        return result.invoke(Any())
    }

    private suspend fun saveGlobalInfo(responseBody: ResponseBody) {
        try {
            val responseData: String = responseBody.string()
            var responseJson = JSONObject(responseData)
            if (responseJson.has("data")) {
                responseJson = responseJson.getJSONObject("data")
            } else {
                return
            }
            val activeCCurrencies: Int = if (responseJson.has("active_cryptocurrencies")) {
                responseJson.getInt("active_cryptocurrencies")
            } else 0
            val upcomingIco: Int = if (responseJson.has("upcoming_icos")) {
                responseJson.getInt("upcoming_icos")
            } else 0
            val ongoingIco: Int = if (responseJson.has("ongoing_icos")) {
                responseJson.getInt("ongoing_icos")
            } else 0
            val endedIco: Int = if (responseJson.has("ended_icos")) {
                responseJson.getInt("ended_icos")
            } else 0
            val marketsNumber: Int = if (responseJson.has("markets")) {
                responseJson.getInt("markets")
            } else 0
            val marketCap24hChangePercentage: Double =
                if (responseJson.has("market_cap_change_percentage_24h_usd")) {
                    responseJson.getDouble("market_cap_change_percentage_24h_usd")
                } else 0.0
            val updateAt: Long = if (responseJson.has("updated_at")) {
                responseJson.getLong("updated_at")
            } else 0L

            val mCapPercentageItems: ArrayList<MarketCapPercentageItem> = arrayListOf()

            if (responseJson.has("market_cap_percentage")) {
                val mCapPercentageJSon: JSONObject =
                    responseJson.getJSONObject("market_cap_percentage")
                val keys: MutableIterator<String> = mCapPercentageJSon.keys()
                keys.forEach { k ->
                    val item = MarketCapPercentageItem(
                        coinId = k,
                        cap = mCapPercentageJSon.getDouble(k)
                    )
                    mCapPercentageItems.add(item)
                }
            }

            val globalInfo = GlobalMarketInfo(
                activeCryptocurrencies = activeCCurrencies,
                endedIcos = endedIco,
                upcomingIcos = upcomingIco,
                ongoingIcos = ongoingIco,
                markets = marketsNumber,
                marketCapChangePercentage24hUsd = marketCap24hChangePercentage,
                marketCapPercentages = mCapPercentageItems.toList(),
                updatedAt = updateAt
            )

            withContext(Dispatchers.IO) {
                localDataSource.clearGlobalMarketInfo()
                localDataSource.putGlobalInfo(info = globalInfo)
            }

        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}