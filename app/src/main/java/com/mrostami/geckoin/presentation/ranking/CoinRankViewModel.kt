package com.mrostami.geckoin.presentation.ranking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.mrostami.geckoin.domain.base.Result
import com.mrostami.geckoin.domain.usecases.MarketRanksUseCase
import com.mrostami.geckoin.model.RankedCoin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinRankViewModel @Inject constructor(
    private val cryptoRanksUseCase: MarketRanksUseCase
) : ViewModel() {

    val rankedCoinsState: MutableStateFlow<Result<PagingData<RankedCoin>>> =
        MutableStateFlow(Result.Empty)

    fun getPagedRankedCoins(refresh: Boolean = false) {
        viewModelScope.launch {
            val result = cryptoRanksUseCase.invoke(
                coroutineDispatcher = Dispatchers.IO
            )
            rankedCoinsState.emitAll(result)
        }
    }
}