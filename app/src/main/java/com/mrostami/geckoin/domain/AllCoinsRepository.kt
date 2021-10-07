package com.mrostami.geckoin.domain

import androidx.paging.PagingData
import com.mrostami.geckoin.model.Coin
import com.mrostami.geckoin.model.RankedCoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface AllCoinsRepository {
    fun syncAllCoins(): Flow<Result<List<Coin>>> = flow { }
    fun searchCoins(searchInput: String?): Flow<Result<PagingData<RankedCoin>>> = flow { }
}