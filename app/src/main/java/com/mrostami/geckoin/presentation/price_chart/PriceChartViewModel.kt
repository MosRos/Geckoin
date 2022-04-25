package com.mrostami.geckoin.presentation.price_chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrostami.geckoin.domain.base.Result
import com.mrostami.geckoin.domain.usecases.PriceChartUseCase
import com.mrostami.geckoin.model.PriceEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PriceChartViewModel @Inject constructor(
    private val priceChartUseCase: PriceChartUseCase
) : ViewModel() {

    val priceChartInfoState: MutableStateFlow<Result<List<PriceEntry>>> = MutableStateFlow(Result.Empty)
    fun getPriceChartInfo(coinId: String) {
        viewModelScope.launch( Dispatchers.IO) {
            val result = priceChartUseCase.invoke(coinId)
            priceChartInfoState.emitAll(result)
        }
    }
}