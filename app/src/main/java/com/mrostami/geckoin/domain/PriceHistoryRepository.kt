package com.mrostami.geckoin.domain

import com.mrostami.geckoin.domain.base.Result
import com.mrostami.geckoin.model.PriceEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface PriceHistoryRepository {
    fun getPriceHistory(
        coinId: String
    ) : Flow<Result<List<PriceEntry>>> = flow {  }
}