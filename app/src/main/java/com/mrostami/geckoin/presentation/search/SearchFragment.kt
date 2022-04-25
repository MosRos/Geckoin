package com.mrostami.geckoin.presentation.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mrostami.geckoin.R
import com.mrostami.geckoin.databinding.SearchFragmentBinding
import com.mrostami.geckoin.model.Coin
import com.mrostami.geckoin.presentation.coin_details.CoinDetailsFragmentDirections
import com.mrostami.geckoin.presentation.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.search_fragment) {

    private val binding: SearchFragmentBinding by viewBinding(SearchFragmentBinding::bind)
    val viewModel: SearchViewModel by viewModels()

    private val DELAY: Long = 650
    private var searchJob = SupervisorJob()
    private val searchScope = CoroutineScope(Dispatchers.Main + searchJob)

    private var coinsRecycler: RecyclerView? = null
    private var coinsAdapter: CoinsAdapter? = null
    private val onCoinClicked: (Coin) -> Unit = { coin ->
        if (coin.id != null) {
            val coinDetailsDirection =
                CoinDetailsFragmentDirections.actionGlobalCoinDetails(coinId = coin.id)
            Navigation.findNavController(binding.rootLayout).navigate(coinDetailsDirection)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWidgets()
        setListeners()
        setObservers()
        requestForData()
    }

    private fun requestForData() {
        lifecycleScope.launch {
            delay(200)
            viewModel.searchCoins("")
        }
    }

    private fun setObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // collect search result
                launch {
                    viewModel.searchResultsStateFlow.collect { result ->
                        updateCoinsAdapter(result)
                    }
                }

                // collect paging adapter loading state
                launch {
                    coinsAdapter?.loadStateFlow?.collect { loadingState ->
                        if (loadingState.source.append == LoadState.Loading) {
                            binding.searchProgress.isVisible = true
                        } else {
                            delay(700)
                            binding.searchProgress.isVisible = false
                        }
                    }
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
        coinsAdapter?.submitData(viewLifecycleOwner.lifecycle, coins)
//        coinsAdapter?.submitData(coins)
        binding.progressBar.isVisible = false
    }
}