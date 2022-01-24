package com.mrostami.geckoin.presentation.coin_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrostami.geckoin.domain.base.Result
import com.mrostami.geckoin.domain.usecases.BitcoinChartInfoUseCase
import com.mrostami.geckoin.domain.usecases.CoinDetailsUseCase
import com.mrostami.geckoin.model.CoinDetailsInfo
import com.mrostami.geckoin.model.PriceEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinDetailsViewModel @Inject constructor(
    private val coinDetailsUseCase: CoinDetailsUseCase
) : ViewModel() {

    val coinInfoState: MutableStateFlow<Result<CoinDetailsInfo>> = MutableStateFlow(Result.Empty)
    fun getDetailsInfo(coinId: String) {
        viewModelScope.launch( Dispatchers.IO) {
            val result = coinDetailsUseCase.invoke(coinId)
            coinInfoState.emitAll(result)
        }
    }
}