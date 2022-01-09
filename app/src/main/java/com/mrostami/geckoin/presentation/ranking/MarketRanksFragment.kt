package com.mrostami.geckoin.presentation.ranking

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.paging.map
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mrostami.geckoin.R
import com.mrostami.geckoin.databinding.MarketRankFragmentBinding
import com.mrostami.geckoin.domain.base.Result
import com.mrostami.geckoin.model.RankedCoin
import com.mrostami.geckoin.presentation.utils.showSnack
import com.mrostami.geckoin.presentation.utils.showToast
import com.mrostami.geckoin.presentation.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MarketRanksFragment : Fragment(R.layout.market_rank_fragment) {

    private val binding: MarketRankFragmentBinding by viewBinding(MarketRankFragmentBinding::bind)
    val viewModel: CoinRankViewModel by viewModels()

    private var rankRecycler: RecyclerView? = null
    private var marketRanksAdapter: RankedCoinsAdapter? = null
    private val onRankedItemClicked: (RankedCoin, Int) -> Unit = { coin, i ->
        context?.showToast("clicked ${coin.name} + $i")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getPagedRankedCoins()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWidgets()
        setObservers()
    }

    private fun setObservers() {
        Timber.e("Start Observing values")
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.rankedCoinsState.collect{ result ->
                when(result) {
                    is Result.Success -> {
                        Timber.e("Collected Size is: ${result.toString()}")
                        updateRanksAdapter(result.data)
                        binding.progressBar.isVisible = false
                    }
                    is Result.Error -> {
                        binding.progressBar.isVisible = false
                        activity?.showSnack(
                            message = result.message ?: result.exception.message ?: "An Error occurred"
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
    }

    private fun initWidgets() {

        marketRanksAdapter = RankedCoinsAdapter(onRankedItemClicked)

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