package com.mrostami.geckoin.domain.usecases

import androidx.paging.PagingData
import com.mrostami.geckoin.domain.AllCoinsRepository
import com.mrostami.geckoin.domain.base.Result
import com.mrostami.geckoin.model.Coin
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class SearchCoinsUseCase @Inject constructor(
    private val allCoinsRepository: AllCoinsRepository
) {
    operator fun invoke(
        query: String?
    ): Flow<PagingData<Coin>> {
        return allCoinsRepository.searchCoins(searchInput = query)
    }
}