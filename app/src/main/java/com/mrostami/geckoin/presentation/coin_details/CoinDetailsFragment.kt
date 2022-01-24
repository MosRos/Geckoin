package com.mrostami.geckoin.presentation.coin_details

import android.os.Bundle
import android.view.View
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import coil.load
import coil.request.CachePolicy
import coil.transform.CircleCropTransformation
import com.mrostami.geckoin.R
import com.mrostami.geckoin.databinding.CoinDetailsFragmentBinding
import com.mrostami.geckoin.domain.base.Result
import com.mrostami.geckoin.model.CoinDetailsInfo
import com.mrostami.geckoin.model.SimplePriceInfo
import com.mrostami.geckoin.presentation.price_chart.PriceChartFragment
import com.mrostami.geckoin.presentation.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class CoinDetailsFragment : Fragment(R.layout.coin_details_fragment) {

    private val binding: CoinDetailsFragmentBinding by viewBinding(CoinDetailsFragmentBinding::bind)
    val viewModel: CoinDetailsViewModel by viewModels()

    private val navargs: CoinDetailsFragmentArgs by navArgs()
    private var coinId: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        coinId = navargs.coinId
        if (coinId.isNullOrEmpty()) {
            Navigation.findNavController(view).popBackStack()
        }
        registerWidgets()
        setObservers()
        requestForData()
    }

    private fun registerWidgets() {
        coinId?.let { id ->
            val chartFragment = PriceChartFragment.newInstance(coinId = id)
            childFragmentManager
                .beginTransaction()
                .add(R.id.chartFragContainer, chartFragment, PriceChartFragment.FRAG_TAG)
                .commit()
        }
    }

    private fun setObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.coinInfoState.collect { result ->
                when (result) {
                    is Result.Loading -> {
                        delay(500)
                        binding.topProgress.isVisible = true
                    }
                    is Result.Error -> {
                        binding.topProgress.isVisible = false
                        result.message?.let { msg ->
                            context?.showToast(message = msg)
                        }
                    }
                    is Result.Success -> {
                        binding.topProgress.isVisible = false
                        updateCoinInfo(result.data)
                    }
                    is Result.Empty -> {
                        binding.topProgress.isVisible = false
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.simplePriceInfoState.collectLatest { result ->
                when (result) {
                    is Result.Loading -> {
                        delay(500)
                    }
                    is Result.Error -> {
//                        binding.topProgress.isVisible = false
                        result.message?.let { msg ->
                            context?.showToast(message = msg)
                        }
                    }
                    is Result.Success -> {
//                        binding.topProgress.isVisible = false
                        updatePriceInfo(result.data)
                    }
                    is Result.Empty -> {
//                        binding.topProgress.isVisible = false
                    }
                }
            }
        }
    }

    private fun requestForData() {
        coinId?.let { id ->
            viewModel.coinId = id
            viewModel.getDetailsInfo(coinId = id)
            viewModel.getPriceInfo(coinId = id)
        }
    }

    private fun updateCoinInfo(coinInfo: CoinDetailsInfo?) {
        if (coinInfo == null) return

        with(binding) {
            imgCoinLogo.load(coinInfo.imageUrl) {
                networkCachePolicy(CachePolicy.ENABLED)
                diskCachePolicy(CachePolicy.ENABLED)
                networkCachePolicy(CachePolicy.ENABLED)
                crossfade(true)
                placeholder(R.drawable.placeholder_character)
                transformations(CircleCropTransformation())
            }
            coinInfo.description?.let { description ->
                txtDescription.text = HtmlCompat.fromHtml(
                    "<h2>About ${coinInfo.name}</h2><br><p>${coinInfo.description}</p>",
                    HtmlCompat.FROM_HTML_MODE_COMPACT
                )
            }
            txtCoinName.text = coinInfo.name + " (${coinInfo.symbol}) "
            txtUsdMarketCap.text = "# ${coinInfo.marketCapRank}"
        }
    }

    private fun updatePriceInfo(priceInfo: SimplePriceInfo?) {
        if (priceInfo == null) return
        with(binding) {
            txtCoinPrice.text = "$ " + priceInfo.usd?.toReadablePrice()
            txtPricePercentChange.text = priceInfo.usd24hChange?.round(decimals = 3).toString()
            txtPricePercentChange.applyPriceStateTextColor(priceInfo.usd24hChange)
            txtUsd24hVol.text = "$ " + priceInfo.usd24hVol?.toReadablePrice()
            txtUsdMarketCap.text = "$ " + priceInfo.usdMarketCap?.toReadablePrice()
        }
    }
}