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
    private val marketRanksMediator: MarketRanksMediator
) : MarketRanksRepository {


    @OptIn(ExperimentalPagingApi::class)
    override fun getRanks(): Flow<PagingData<RankedCoin>> {

        val pagingSourceFactory = { localDataSource.getPagedRankedCoins() }
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = 1,
                enablePlaceholders = false,
                initialLoadSize = 50,
                maxSize = 200
            ),
            initialKey = 1,
            remoteMediator = marketRanksMediator,
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }
}