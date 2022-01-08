package com.mrostami.geckoin.domain.usecases

import com.mrostami.geckoin.domain.GlobalInfoRepository
import com.mrostami.geckoin.domain.base.Result
import com.mrostami.geckoin.domain.base.SimpleFlowUseCase
import com.mrostami.geckoin.model.BitcoinPriceInfo
import com.mrostami.geckoin.model.SimplePriceInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BitcoinSimplePriceUseCase @Inject constructor(
    private val globalInfoRepository: GlobalInfoRepository
) : SimpleFlowUseCase<BitcoinPriceInfo>(coroutineDispatcher = Dispatchers.IO) {

    override fun execute(refresh: Boolean): Flow<Result<BitcoinPriceInfo>> {
        return globalInfoRepository.getBtcDailyPriceInfo(forceRefresh = refresh)
    }
}