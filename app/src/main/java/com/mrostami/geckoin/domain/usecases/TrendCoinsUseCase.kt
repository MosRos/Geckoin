package com.mrostami.geckoin.domain.usecases

import com.mrostami.geckoin.domain.GlobalInfoRepository
import com.mrostami.geckoin.domain.base.Result
import com.mrostami.geckoin.domain.base.SimpleFlowUseCase
import com.mrostami.geckoin.model.TrendCoin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TrendCoinsUseCase @Inject constructor(
    private val globalInfoRepository: GlobalInfoRepository
) : SimpleFlowUseCase<List<TrendCoin>>(coroutineDispatcher = Dispatchers.IO){
    override fun execute(refresh: Boolean): Flow<Result<List<TrendCoin>>> {
        return globalInfoRepository.getTrendingCoins(forceRefresh = refresh)
    }
}