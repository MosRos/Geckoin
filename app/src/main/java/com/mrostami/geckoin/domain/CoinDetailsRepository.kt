package com.mrostami.geckoin.domain

import com.mrostami.geckoin.domain.base.Result
import com.mrostami.geckoin.model.CoinDetailsInfo
import com.mrostami.geckoin.model.SimplePriceInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface CoinDetailsRepository {
    fun getCoinDetails(coinId: String) : Flow<Result<CoinDetailsInfo>> = flow {  }
    fun getSimplePriceInfo(coinId: String) : Flow<Result<SimplePriceInfo>> = flow {  }
}