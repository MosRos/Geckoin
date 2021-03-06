package com.mrostami.geckoin.presentation.ranking

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mrostami.geckoin.R
import com.mrostami.geckoin.databinding.MarketRankFragmentBinding
import com.mrostami.geckoin.domain.base.Result
import com.mrostami.geckoin.model.RankedCoin
import com.mrostami.geckoin.presentation.coin_details.CoinDetailsFragmentDirections
import com.mrostami.geckoin.presentation.utils.showSnack
import com.mrostami.geckoin.presentation.utils.showToast
import com.mrostami.geckoin.presentation.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MarketRanksFragment : Fragment(R.layout.market_rank_fragment) {

    private val binding: MarketRankFragmentBinding by viewBinding(MarketRankFragmentBinding::bind)
    val viewModel: CoinRankViewModel by viewModels()

    private var rankRecycler: RecyclerView? = null
    private var marketRanksAdapter: RankedCoinsAdapter? = null
    private val onRankedItemClicked: (RankedCoin, Int) -> Unit = { coin, i ->
        if (coin.id != null) {
            val coinDetailsDirection =
                CoinDetailsFragmentDirections.actionGlobalCoinDetails(coinId = coin.id)
            Navigation.findNavController(binding.root).navigate(coinDetailsDirection)
        }
    }
    private val onRanksRetryClicked: () -> Unit = {
        context?.showToast("retry clicked")
    }
    private var ranksLoadingStateAdapter: RanksLoadingStateAdapter? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWidgets()
        setObservers()
        requestForData()
    }

    private fun requestForData() {
        viewModel.getPagedRankedCoins()
    }

    private fun setObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // collect ranks
                launch {
                    collectPagedRanks()
                }

                // collect paging loading state
                launch {
                   collectPagingLoadingState()
                }
            }
        }
    }

    private suspend fun collectPagedRanks() {
        viewModel.rankedCoinsStateFlow.collectLatest { result ->
            when (result) {
                is Result.Success -> {
                    updateRanksAdapter(result.data)
                    binding.progressBar.isVisible = false
                }
                is Result.Error -> {
                    binding.progressBar.isVisible = false
                    activity?.showSnack(
                        message = result.message ?: result.exception.message
                        ?: "An Error occurred"
                    )
                }
                is Result.Loading -> {
                    binding.progressBar.isVisible = true
                }
                else -> {
                    binding.progressBar.isVisible = true
                }
            }
        }
    }

    private suspend fun collectPagingLoadingState() {
        marketRanksAdapter?.loadStateFlow?.collectLatest { loadingState ->
            ranksLoadingStateAdapter?.loadState = loadingState.source.append
            if (loadingState.source.append == LoadState.Loading) {
                binding.pagingProgress.isVisible = true
            } else {
                delay(1000)
                binding.pagingProgress.isVisible = false
            }
        }
    }

    private fun initWidgets() {
        ranksLoadingStateAdapter =
            RanksLoadingStateAdapter(onRanksRetryClicked, viewLifecycleOwner.lifecycleScope)
        marketRanksAdapter = RankedCoinsAdapter(onRankedItemClicked).apply {
            ranksLoadingStateAdapter?.let {
                withLoadStateFooter(
                    it
                )
            }
            addLoadStateListener { combinedLoadStates ->
                ranksLoadingStateAdapter?.loadState = combinedLoadStates.source.append
            }
        }


        val llManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val dividerDrawable: Drawable? = context?.let { ctx ->
            ContextCompat.getDrawable(ctx, R.drawable.line_divider)
        }
        rankRecycler = binding.rankRecycler
        rankRecycler?.layoutManager = llManager

        val verticalBottomDecoration = DividerItemDecoration(
            rankRecycler?.context,
            llManager.orientation
        )
        dividerDrawable?.let { verticalBottomDecoration.setDrawable(it) }
        rankRecycler?.addItemDecoration(verticalBottomDecoration)
        rankRecycler?.adapter = marketRanksAdapter
    }

    private fun updateRanksAdapter(coins: PagingData<RankedCoin>) {
        viewLifecycleOwner.lifecycleScope.launch {
            marketRanksAdapter?.submitData(coins)
        }
    }
}