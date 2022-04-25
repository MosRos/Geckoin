package com.mrostami.geckoin.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mrostami.geckoin.domain.usecases.SearchCoinsUseCase
import com.mrostami.geckoin.model.Coin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchCoinsUseCase: SearchCoinsUseCase
) : ViewModel() {

    private val searchResultsState: MutableStateFlow<PagingData<Coin>> = MutableStateFlow(PagingData.empty())
    val searchResultsStateFlow: StateFlow<PagingData<Coin>> = searchResultsState
    fun searchCoins(searchInput: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = searchCoinsUseCase.invoke(
                query = searchInput
            ).cachedIn(viewModelScope)
            searchResultsState.emitAll(result)
        }
    }

}