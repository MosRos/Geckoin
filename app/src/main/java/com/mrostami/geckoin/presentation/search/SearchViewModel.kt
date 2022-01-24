package com.mrostami.geckoin.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.mrostami.geckoin.domain.usecases.SearchCoinsUseCase
import com.mrostami.geckoin.model.Coin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchCoinsUseCase: SearchCoinsUseCase
    ) : ViewModel() {

    val searchResultsState: MutableStateFlow<PagingData<Coin>> = MutableStateFlow(PagingData.empty())
    fun searchCoins(searchInput: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = searchCoinsUseCase.invoke(
                query = searchInput
            )
            searchResultsState.emitAll(result)
        }
    }

}