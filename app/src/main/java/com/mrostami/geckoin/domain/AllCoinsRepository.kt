/*
 * *
 *  * Created by Moslem Rostami on 4/14/20 9:21 AM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 4/14/20 9:21 AM
 *
 */

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