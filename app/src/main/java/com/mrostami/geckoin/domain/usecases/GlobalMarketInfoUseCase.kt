package com.mrostami.geckoin.domain.usecases

import com.mrostami.geckoin.domain.GlobalInfoRepository
import com.mrostami.geckoin.domain.base.FlowUseCase
import com.mrostami.geckoin.domain.base.Result
import com.mrostami.geckoin.domain.base.SimpleFlowUseCase
import com.mrostami.geckoin.model.GlobalMarketInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GlobalMarketInfoUseCase @Inject constructor(
    private val globalInfoRepository: GlobalInfoRepository
) : SimpleFlowUseCase<GlobalMarketInfo>(coroutineDispatcher = Dispatchers.IO) {

    override fun execute(refresh: Boolean): Flow<Result<GlobalMarketInfo>> {
        return globalInfoRepository.getMarketInfo(forceRefresh = refresh)
    }
}