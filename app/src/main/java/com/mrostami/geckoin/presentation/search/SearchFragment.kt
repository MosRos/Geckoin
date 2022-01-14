package com.mrostami.geckoin.presentation.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mrostami.geckoin.R
import com.mrostami.geckoin.databinding.SearchFragmentBinding
import com.mrostami.geckoin.domain.base.Result
import com.mrostami.geckoin.model.Coin
import com.mrostami.geckoin.presentation.utils.showSnack
import com.mrostami.geckoin.presentation.utils.showToast
import com.mrostami.geckoin.presentation.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SearchFragment: Fragment(R.layout.search_fragment) {

    private val binding: SearchFragmentBinding by viewBinding(SearchFragmentBinding::bind)
    val viewModel: SearchViewModel by viewModels()

    private var coinsRecycler: RecyclerView? = null
    private var coinsAdapter: CoinsAdapter? = null
    private val onCoinClicked: (Coin) -> Unit =  { coin->
        context?.showToast( "${coin.name} + ${coin.symbol} clicked", Toast.LENGTH_SHORT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            delay(200)
            viewModel.searchCoins("")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWidgets()
        setListeners()
        setObservers()
    }

    private fun setObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchResultsState.collectLatest { result ->
                updateCoinsAdapter(result)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            coinsAdapter?.loadStateFlow?.collectLatest { loadingState ->
                if (loadingState.source.append == LoadState.Loading) {
                    binding.searchProgress.isVisible = true
                } else {
                    delay(700)
                    binding.searchProgress.isVisible = false
                }
            }
        }
    }

    private fun initWidgets() {
        val noOfColumns = resources.getInteger(R.integer.coins_grid_columns)
        val gridLM = GridLayoutManager(requireContext(), noOfColumns, RecyclerView.VERTICAL, false)
        coinsRecycler = binding.coinsRecycler
        coinsRecycler?.layoutManager = gridLM

        coinsAdapter = CoinsAdapter(onCoinClicked)
        coinsRecycler?.adapter = coinsAdapter
    }

    private fun setListeners() {
        binding.searchEdt.addTextChangedListener(object : TextWatcher {

            private val DELAY: Long = 650
            var searchJob = SupervisorJob()
            val searchScope = CoroutineScope(Dispatchers.Main + searchJob)

            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchScope.coroutineContext.cancelChildren()
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchScope.launch {
                    delay(DELAY)
                    doSearch(p0.toString().trim())
                }
            }
        })
    }

    private fun doSearch(input: String) {
            viewModel.searchCoins(input)
    }

    private suspend fun updateCoinsAdapter(coins: PagingData<Coin>) {
        coinsAdapter?.submitData(coins)
        binding.progressBar.isVisible = false
    }
}