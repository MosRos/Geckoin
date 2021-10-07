package com.mrostami.geckoin.domain

import androidx.paging.PagingData
import com.mrostami.geckoin.model.RankedCoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface MarketRanksRepository {
    fun getRanks() : Flow<Result<PagingData<RankedCoin>>> = flow {  }
}