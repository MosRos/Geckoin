package com.mrostami.geckoin.data.remote

import com.haroldadmin.cnradapter.NetworkResponse
import com.mrostami.geckoin.data.remote.responses.CoinGeckoApiError
import com.mrostami.geckoin.data.remote.responses.CoinGeckoPingResponse
import com.mrostami.geckoin.data.remote.responses.PriceChartResponse
import com.mrostami.geckoin.data.remote.responses.TrendCoinsResponse
import com.mrostami.geckoin.model.Coin
import com.mrostami.geckoin.model.CoinDetailResponse
import com.mrostami.geckoin.model.RankedCoin
import com.mrostami.geckoin.model.SimplePriceInfoResponse
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinGeckoService {

    // Ping for api connection checking
    @GET("ping")
    suspend fun checkCoinGeckoConnection() : NetworkResponse<CoinGeckoPingResponse, CoinGeckoApiError>

    // All coins list
    @GET("coins/list")
    suspend fun getAllCoins() : NetworkResponse<List<Coin>, CoinGeckoApiError>

    // Global market info
    @GET("global")
    suspend fun getGlobalMarketInfo() : NetworkResponse<ResponseBody, CoinGeckoApiError>

    // Top Search and Trend coins
    @GET("search/trending")
    suspend fun getTrendingCoins() : NetworkResponse<TrendCoinsResponse, CoinGeckoApiError>

    // Price Info for a coin
    @GET("simple/price")
    suspend fun getSimplePrice(
        @Query("ids") ids: String = "bitcoin",
        @Query("vs_currencies") vsCurrency: String = "usd",
        @Query("include_market_cap") includeMarketCap: Boolean = true,
        @Query("include_24hr_vol") include24HrVolume: Boolean = true,
        @Query("include_24hr_change") include24HrChange: Boolean = true,
        @Query("include_last_updated_at") includeLastUpdateTime: Boolean = true
    ) : NetworkResponse<SimplePriceInfoResponse, CoinGeckoApiError>

    // Price chart Info
    @GET("coins/{id}/market_chart")
    suspend fun getMarketChartInfo(
        @Path("id") coinId: String,
        @Query("vs_currency") vsCurrency: String = "usd",
        @Query("days") days: Int = 10,
        @Query("interval") interval: String = "daily"
    ) : NetworkResponse<PriceChartResponse, CoinGeckoApiError>

    // Market Ranks
    @GET("coins/markets")
    suspend fun getPagedMarketRanks(
        @Query("vs_currency") vs_currency: String = "usd",
        @Query("page") page: Int,
        @Query("per_page") per_page: Int
    ) : List<RankedCoin>

    // Coin Detail info
    @GET("coins/{id}?localization=false&tickers=false&market_data=false&community_data=false&developer_data=false&sparkline=true")
    suspend fun getCoinDetailsInfo(
        @Path("id") coinId: String
    ) : NetworkResponse<CoinDetailResponse, CoinGeckoApiError>
}