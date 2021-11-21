package com.mrostami.geckoin.data.remote

import com.haroldadmin.cnradapter.NetworkResponse
import com.mrostami.geckoin.data.remote.responses.CoinGeckoApiError
import com.mrostami.geckoin.data.remote.responses.CoinGeckoPingResponse
import com.mrostami.geckoin.data.remote.responses.TrendCoinsResponse
import okhttp3.ResponseBody
import retrofit2.http.GET

interface CoinGeckoService {

    @GET("ping")
    suspend fun checkCoinGeckoConnection() : NetworkResponse<CoinGeckoPingResponse, CoinGeckoApiError>

    @GET("global")
    suspend fun getGlobalMarketInfo() : NetworkResponse<ResponseBody, CoinGeckoApiError>

    @GET("search/trending")
    suspend fun getTrendingCoins() : NetworkResponse<TrendCoinsResponse, CoinGeckoApiError>
}