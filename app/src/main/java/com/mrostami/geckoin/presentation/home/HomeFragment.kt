package com.mrostami.geckoin.presentation.home

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.mrostami.geckoin.R
import com.mrostami.geckoin.databinding.HomeFragmentBinding
import com.mrostami.geckoin.domain.base.Result
import com.mrostami.geckoin.model.BitcoinPriceInfo
import com.mrostami.geckoin.model.GlobalMarketInfo
import com.mrostami.geckoin.presentation.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.home_fragment) {

    private val binding: HomeFragmentBinding by viewBinding(HomeFragmentBinding::bind)
    val viewModel: HomeViewModel by viewModels<HomeViewModel>()

    private var bitcoinPriceChart: LineChart? = null
    private var dominancePieChart: PieChart? = null
    private var trendCoinsRecycler: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getBitcoinPriceInfo(forceRefresh = true)
        viewModel.getBitcoinChartInfo(forceRefresh = true)
        viewModel.getGlobalInfo(forceRefresh = true)
        viewModel.getTrendCoins(forceRefresh = true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerWidgets()
        setObservables()
    }

    private fun registerWidgets() {
        bitcoinPriceChart = binding.bitcoinPriceChart
        dominancePieChart = binding.dominancePieChart
        trendCoinsRecycler = binding.trendCoinsRecycler

        initDominancePieChart()
    }

    private fun setObservables() {

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.bitcoinPriceInfoState.collect { result ->
                when(result) {
                    is Result.Empty -> {
//                        binding.progressBar.isVisible = false
                    }
                    is Result.Success -> {
//                        binding.progressBar.isVisible = false
                        updateBitcoinPriceInfo(result.data)
                        Timber.e("Bitcoin Price Info Result is: ${result.data}")
                    }
                    is Result.Error -> {
//                        binding.progressBar.isVisible = false
                        Timber.e("Bitcoin Price Info Error: ${result.message}")
                        Toast.makeText(requireContext(), "Error getting Bitcoin info: ${result.message}", Toast.LENGTH_SHORT).show()
                    }
                    is Result.Loading -> {
//                        binding.progressBar.isVisible = true
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.bitcoinChartInfoState.collect { result ->
                when(result) {
                    is Result.Empty -> {
                        binding.bitcoinProgressBar.isVisible = false
                    }
                    is Result.Success -> {
                        binding.bitcoinProgressBar.isVisible = false
                        Timber.e("Success Bitcoin Chart Result is: ${result.data}")
                    }
                    is Result.Error -> {
                        binding.bitcoinProgressBar.isVisible = false
                        Timber.e("Bitcoin Chart Error: ${result.message}")
                        Toast.makeText(requireContext(), "Error getting Bitcoin Chart info: ${result.message}", Toast.LENGTH_SHORT).show()
                    }
                    is Result.Loading -> {
                        binding.bitcoinProgressBar.isVisible = true
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.marketInfoState.collect { result ->
                when(result) {
                    is Result.Empty -> {
                        binding.dominanceProgressBar.isVisible = false
                    }
                    is Result.Success -> {
                        binding.dominanceProgressBar.isVisible = false
                        Timber.e("Success globalInfo Result is: ${result.data}")
                    }
                    is Result.Error -> {
                        binding.dominanceProgressBar.isVisible = false
                        Timber.e("Global Info Error: ${result.message}")
                        Toast.makeText(requireContext(), "Error getting global info: ${result.message}", Toast.LENGTH_SHORT).show()
                    }
                    is Result.Loading -> {
                        binding.dominanceProgressBar.isVisible = true
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.trendCoinsState.collect { result ->
                when(result) {
                    is Result.Empty -> {
                        binding.trendCoinsProgressBar.isVisible = false
                    }
                    is Result.Success -> {
                        binding.trendCoinsProgressBar.isVisible = false
                        Timber.e("TrendCoins Result is: ${result.data}")
                    }
                    is Result.Error -> {
                        binding.trendCoinsProgressBar.isVisible = false
                        Timber.e("TrendCoins Error: ${result.message}")
                        Toast.makeText(requireContext(), "Error Getting Trend coins: ${result.message}", Toast.LENGTH_SHORT).show()
                    }
                    is Result.Loading -> {
                        binding.trendCoinsProgressBar.isVisible = true
                    }
                }
            }
        }
    }

    private fun updateBitcoinPriceInfo(priceInfo: BitcoinPriceInfo) {
        binding.imgBitcoin.load(R.drawable.bitcoin_logo)
        binding.txtBtcPrice.text = priceInfo.info.usd?.toString()
        binding.txtPricePercentChange.text = priceInfo.info.usd24hChange?.toString()
        binding.txtBtc24hVol.text = "Vol " + priceInfo.info.usd24hVol?.toString()
        binding.txtBtc24hCap.text = "Cap " + priceInfo.info.usdMarketCap?.toString()
    }

    private fun initDominancePieChart(){
        val firaSans = ResourcesCompat.getFont(requireContext(), R.font.firasans_regular)
        val firaSansLight = ResourcesCompat.getFont(requireContext(), R.font.firasans_light)
        dominancePieChart?.apply {
            description?.isEnabled = false
            legend?.isEnabled = false
            setCenterTextTypeface(firaSans)
            setUsePercentValues(true)
            setDrawCenterText(true)
            isDrawHoleEnabled = true
            setHoleColor(Color.TRANSPARENT)
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(110)
            holeRadius = 50f
            transparentCircleRadius = 55f

            setExtraOffsets(3f, 3f, 3f, 3f)
            rotationAngle = 0f

            // enable rotation of the chart by touch
            isRotationEnabled = true
            isHighlightPerTapEnabled = true
            dragDecelerationFrictionCoef = 0.8f
            animateY(1000, Easing.EaseInOutQuad)
        }
        val leg = dominancePieChart?.legend?.apply {
            verticalAlignment = Legend.LegendVerticalAlignment.TOP
            horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            orientation = Legend.LegendOrientation.VERTICAL
            setDrawInside(false)
            isEnabled = false
            typeface = firaSansLight
            textSize = 10f
        }
    }

    private fun updateDominanceChart(marketInfo: GlobalMarketInfo) {
        marketInfo.marketCapPercentages?.let { caps ->

        }
    }
}