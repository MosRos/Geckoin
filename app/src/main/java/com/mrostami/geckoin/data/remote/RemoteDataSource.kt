package com.mrostami.geckoin.data.remote

import com.haroldadmin.cnradapter.NetworkResponse
import com.mrostami.geckoin.data.remote.responses.CoinGeckoApiError
import com.mrostami.geckoin.data.remote.responses.CoinGeckoPingResponse
import com.mrostami.geckoin.data.remote.responses.PriceChartResponse
import com.mrostami.geckoin.data.remote.responses.TrendCoinsResponse
import com.mrostami.geckoin.model.Coin
import com.mrostami.geckoin.model.CoinDetailResponse
import com.mrostami.geckoin.model.RankedCoin
import com.mrostami.geckoin.model.BitcoinSimplePriceInfoResponse
import okhttp3.ResponseBody
import retrofit2.Retrofit
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val retrofit: Retrofit
) : CoinGeckoService {

    private val coinGeckoApiService by lazy {
        retrofit.create(CoinGeckoService::class.java)
    }

    override suspend fun checkCoinGeckoConnection(): NetworkResponse<CoinGeckoPingResponse, CoinGeckoApiError> =
        coinGeckoApiService.checkCoinGeckoConnection()

    override suspend fun getGlobalMarketInfo(): NetworkResponse<ResponseBody, CoinGeckoApiError> =
        coinGeckoApiService.getGlobalMarketInfo()

    override suspend fun getTrendingCoins(): NetworkResponse<TrendCoinsResponse, CoinGeckoApiError> {
        return coinGeckoApiService.getTrendingCoins()
    }

    override suspend fun getBitcoinSimplePrice(
        id: String,
        vsCurrency: String,
        includeMarketCap: Boolean,
        include24HrVolume: Boolean,
        include24HrChange: Boolean,
        includeLastUpdateTime: Boolean
    ): NetworkResponse<BitcoinSimplePriceInfoResponse, CoinGeckoApiError> {
        return coinGeckoApiService.getBitcoinSimplePrice(
            id = id
        )
    }

    override suspend fun getSimplePrice(
        coinId: String,
        vsCurrency: String,
        includeMarketCap: Boolean,
        include24HrVolume: Boolean,
        include24HrChange: Boolean,
        includeLastUpdateTime: Boolean
    ): NetworkResponse<ResponseBody, CoinGeckoApiError> {
        return coinGeckoApiService.getSimplePrice(id = coinId)
    }

    override suspend fun getMarketChartInfo(
        coinId: String,
        vsCurrency: String,
        days: Int,
        interval: String
    ): NetworkResponse<PriceChartResponse, CoinGeckoApiError> {
        return coinGeckoApiService.getMarketChartInfo(coinId = coinId)
    }

    override suspend fun getPagedMarketRanks(
        vs_currency: String,
        page: Int,
        per_page: Int
    ): List<RankedCoin> = coinGeckoApiService.getPagedMarketRanks(
        page = page,
        per_page = per_page
    )

    override suspend fun getAllCoins(): NetworkResponse<List<Coin>, CoinGeckoApiError> =
        coinGeckoApiService.getAllCoins()

    override suspend fun getCoinDetailsInfo(coinId: String): NetworkResponse<CoinDetailResponse, CoinGeckoApiError> {
        return coinGeckoApiService.getCoinDetailsInfo(coinId = coinId)
    }
}