package com.mrostami.geckoin.domain.usecases

import com.mrostami.geckoin.domain.CoinDetailsRepository
import com.mrostami.geckoin.domain.base.FlowUseCase
import com.mrostami.geckoin.domain.base.Result
import com.mrostami.geckoin.model.CoinDetailsInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CoinDetailsUseCase @Inject constructor(
    private val coinDetailsRepository: CoinDetailsRepository
) : FlowUseCase<String, CoinDetailsInfo>(coroutineDispatcher = Dispatchers.IO) {
    override fun execute(parameters: String, refresh: Boolean): Flow<Result<CoinDetailsInfo>> {
        return coinDetailsRepository.getCoinDetails(coinId = parameters)
    }
}