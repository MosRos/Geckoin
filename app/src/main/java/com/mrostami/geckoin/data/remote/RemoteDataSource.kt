package com.mrostami.geckoin.data.remote

import com.haroldadmin.cnradapter.NetworkResponse
import com.mrostami.geckoin.data.remote.responses.CoinGeckoApiError
import com.mrostami.geckoin.data.remote.responses.CoinGeckoPingResponse
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

}