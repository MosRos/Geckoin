package com.mrostami.geckoin.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrostami.geckoin.domain.base.Result
import com.mrostami.geckoin.domain.usecases.BitcoinChartInfoUseCase
import com.mrostami.geckoin.domain.usecases.BitcoinSimplePriceUseCase
import com.mrostami.geckoin.domain.usecases.GlobalMarketInfoUseCase
import com.mrostami.geckoin.domain.usecases.TrendCoinsUseCase
import com.mrostami.geckoin.model.BitcoinPriceInfo
import com.mrostami.geckoin.model.GlobalMarketInfo
import com.mrostami.geckoin.model.PriceEntry
import com.mrostami.geckoin.model.TrendCoin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val globalMarketInfoUseCase: GlobalMarketInfoUseCase,
    private val trendCoinsUseCase: TrendCoinsUseCase,
    private val bitcoinSimplePriceUseCase: BitcoinSimplePriceUseCase,
    private val bitcoinChartInfoUseCase: BitcoinChartInfoUseCase
) : ViewModel() {

    val bitcoinPriceInfoState: MutableStateFlow<Result<BitcoinPriceInfo>> = MutableStateFlow(Result.Empty)
    fun getBitcoinPriceInfo(forceRefresh: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = bitcoinSimplePriceUseCase.invoke(forceRefresh = forceRefresh)
            bitcoinPriceInfoState.emitAll(result)
        }
    }

    val bitcoinChartInfoState: MutableStateFlow<Result<List<PriceEntry>>> = MutableStateFlow(Result.Empty)
    fun getBitcoinChartInfo(forceRefresh: Boolean = false) {
        viewModelScope.launch( Dispatchers.IO) {
            val result = bitcoinChartInfoUseCase.invoke(forceRefresh = forceRefresh)
            bitcoinChartInfoState.emitAll(result)
        }
    }

    val marketInfoState: MutableStateFlow<Result<GlobalMarketInfo>> = MutableStateFlow(Result.Empty)
    fun getGlobalInfo(forceRefresh: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = globalMarketInfoUseCase.invoke(forceRefresh = forceRefresh)
            marketInfoState.emitAll(result)
        }
    }

    val trendCoinsState: MutableStateFlow<Result<List<TrendCoin>>> = MutableStateFlow(Result.Empty)
    fun getTrendCoins(forceRefresh: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = trendCoinsUseCase.invoke(forceRefresh = forceRefresh)
            trendCoinsState.emitAll(result)
        }
    }
}