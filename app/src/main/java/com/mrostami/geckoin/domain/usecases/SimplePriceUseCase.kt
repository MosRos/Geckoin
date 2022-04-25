package com.mrostami.geckoin.domain.usecases

import com.mrostami.geckoin.domain.CoinDetailsRepository
import com.mrostami.geckoin.domain.base.FlowUseCase
import com.mrostami.geckoin.domain.base.Result
import com.mrostami.geckoin.model.SimplePriceInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SimplePriceUseCase @Inject constructor(
    private val coinDetailsRepository: CoinDetailsRepository
) : FlowUseCase<String, SimplePriceInfo>(coroutineDispatcher = Dispatchers.IO){
    override fun execute(parameters: String, refresh: Boolean): Flow<Result<SimplePriceInfo>> {
        return coinDetailsRepository.getSimplePriceInfo(coinId = parameters)
    }
}