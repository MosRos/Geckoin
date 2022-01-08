package com.mrostami.geckoin.domain

import com.mrostami.geckoin.domain.base.Result
import com.mrostami.geckoin.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface GlobalInfoRepository {
    fun getMarketInfo(forceRefresh: Boolean) : Flow<Result<GlobalMarketInfo>> = flow {  }
    fun getTrendingCoins(forceRefresh: Boolean) : Flow<Result<List<TrendCoin>>> = flow {  }
    fun getBtcDailyPriceInfo(forceRefresh: Boolean) : Flow<Result<BitcoinPriceInfo>> = flow {  }
    fun getBtcMarketChartInfo(forceRefresh: Boolean) : Flow<Result<List<PriceEntry>>> = flow {  }
}