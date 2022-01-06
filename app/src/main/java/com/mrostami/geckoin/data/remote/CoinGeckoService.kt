package com.mrostami.geckoin.data.remote

import com.haroldadmin.cnradapter.NetworkResponse
import com.mrostami.geckoin.data.remote.responses.CoinGeckoApiError
import com.mrostami.geckoin.data.remote.responses.CoinGeckoPingResponse
import com.mrostami.geckoin.data.remote.responses.PriceChartResponse
import com.mrostami.geckoin.data.remote.responses.TrendCoinsResponse
import com.mrostami.geckoin.model.SimplePriceInfoResponse
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinGeckoService {

    @GET("ping")
    suspend fun checkCoinGeckoConnection() : NetworkResponse<CoinGeckoPingResponse, CoinGeckoApiError>

    @GET("global")
    suspend fun getGlobalMarketInfo() : NetworkResponse<ResponseBody, CoinGeckoApiError>

    @GET("search/trending")
    suspend fun getTrendingCoins() : NetworkResponse<TrendCoinsResponse, CoinGeckoApiError>

    @GET("simple/price")
    suspend fun getSimplePrice(
        @Query("ids") ids: String = "bitcoin",
        @Query("vs_currencies") vsCurrency: String = "usd",
        @Query("include_market_cap") includeMarketCap: Boolean = true,
        @Query("include_24hr_vol") include24HrVolume: Boolean = true,
        @Query("include_24hr_change") include24HrChange: Boolean = true,
        @Query("include_last_updated_at") includeLastUpdateTime: Boolean = true
    ) : NetworkResponse<SimplePriceInfoResponse, CoinGeckoApiError>

    @GET("coins/{id}/market_chart")
    suspend fun getMarketChartInfo(
        @Path("id") coinId: String,
        @Query("vs_currency") vsCurrency: String = "usd",
        @Query("days") days: Int = 10,
        @Query("interval") interval: String = "daily"
    ) : NetworkResponse<PriceChartResponse, CoinGeckoApiError>
}