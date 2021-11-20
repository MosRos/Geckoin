package com.mrostami.geckoin.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrostami.geckoin.domain.base.Result
import com.mrostami.geckoin.domain.usecases.GlobalMarketInfoUseCase
import com.mrostami.geckoin.model.GlobalMarketInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val globalMarketInfoUseCase: GlobalMarketInfoUseCase
) : ViewModel() {

    val marketInfoState: MutableStateFlow<Result<GlobalMarketInfo>> = MutableStateFlow(Result.Empty)
    fun getGlobalInfo(forceRefresh: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = globalMarketInfoUseCase.invoke(forceRefresh = forceRefresh)
            marketInfoState.emitAll(result)
        }
    }
}