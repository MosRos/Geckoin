package com.mrostami.geckoin.domain.usecases

import com.mrostami.geckoin.domain.GlobalInfoRepository
import com.mrostami.geckoin.domain.base.Result
import com.mrostami.geckoin.domain.base.SimpleFlowUseCase
import com.mrostami.geckoin.model.PriceEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BitcoinChartInfoUseCase @Inject constructor(
    private val globalInfoRepository: GlobalInfoRepository
) : SimpleFlowUseCase<List<PriceEntry>>(coroutineDispatcher = Dispatchers.IO) {

    override fun execute(refresh: Boolean): Flow<Result<List<PriceEntry>>> {
        return globalInfoRepository.getBtcMarketChartInfo(forceRefresh = refresh)
    }
}