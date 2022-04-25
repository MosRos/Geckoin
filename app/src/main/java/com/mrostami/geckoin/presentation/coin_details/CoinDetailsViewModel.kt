package com.mrostami.geckoin.presentation.coin_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrostami.geckoin.domain.base.Result
import com.mrostami.geckoin.domain.usecases.BitcoinChartInfoUseCase
import com.mrostami.geckoin.domain.usecases.CoinDetailsUseCase
import com.mrostami.geckoin.domain.usecases.SimplePriceUseCase
import com.mrostami.geckoin.model.CoinDetailsInfo
import com.mrostami.geckoin.model.PriceEntry
import com.mrostami.geckoin.model.SimplePriceInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinDetailsViewModel @Inject constructor(
    private val coinDetailsUseCase: CoinDetailsUseCase,
    private val simplePriceUseCase: SimplePriceUseCase
) : ViewModel() {

    var coinId: String? = null

    private val coinInfoState: MutableStateFlow<Result<CoinDetailsInfo>> = MutableStateFlow(Result.Empty)
    val coinInfoStateFlow: StateFlow<Result<CoinDetailsInfo>> = coinInfoState
    fun getDetailsInfo(coinId: String) {
        viewModelScope.launch( Dispatchers.IO) {
            val result = coinDetailsUseCase.invoke(coinId)
            coinInfoState.emitAll(result)
        }
    }

    private val simplePriceInfoState: MutableStateFlow<Result<SimplePriceInfo>> = MutableStateFlow(Result.Empty)
    val simplePriceInfoStateFlow: StateFlow<Result<SimplePriceInfo>> = simplePriceInfoState
    fun getPriceInfo(coinId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = simplePriceUseCase.invoke(coinId)
            simplePriceInfoState.emitAll(result)
        }
    }
}