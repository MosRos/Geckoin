/*
 * *
 *  * Created by Moslem Rostami on 6/17/20 5:04 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 6/17/20 5:04 PM
 *
 */

package com.mrostami.geckoin.data.repositories

import androidx.paging.*
import com.mrostami.geckoin.data.local.LocalDataSource
import com.mrostami.geckoin.domain.MarketRanksRepository
import com.mrostami.geckoin.model.RankedCoin
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class MarketRanksRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val marketRanksMediator: MarketRanksMediator) : MarketRanksRepository {

    @ExperimentalPagingApi
    override fun getRanks(): Flow<PagingData<RankedCoin>> {

        val pagingSourceFactory = { localDataSource.getPagedRankedCoins() }
        return Pager<Int, RankedCoin>(
                config = PagingConfig(pageSize = PAGE_SIZE, prefetchDistance = 5, enablePlaceholders = true, initialLoadSize = 100, maxSize = 200),
                remoteMediator = marketRanksMediator,
                initialKey = 1,
                pagingSourceFactory = pagingSourceFactory
            ).flow

        }

}