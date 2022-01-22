package com.mrostami.geckoin.presentation.home

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.mrostami.geckoin.R
import com.mrostami.geckoin.databinding.HomeFragmentBinding
import com.mrostami.geckoin.domain.base.Result
import com.mrostami.geckoin.model.*
import com.mrostami.geckoin.presentation.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.home_fragment) {

    private val binding: HomeFragmentBinding by viewBinding(HomeFragmentBinding::bind)
    val viewModel: HomeViewModel by viewModels<HomeViewModel>()

    private var bitcoinPriceChart: LineChart? = null
    private var dominancePieChart: PieChart? = null
    private var trendCoinsRecycler: RecyclerView? = null
    private var trendsAdapter: TrendCoinsAdapter? = null

    private val onTrendCoinClicked: (TrendCoin, Int) -> Unit = { trendCoin, i ->
        context?.showToast("${trendCoin.name}")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerWidgets()
        setObservables()
        requestForData()
    }

    private fun requestForData() {
        viewModel.getBitcoinPriceInfo()
        viewModel.getBitcoinChartInfo()
        viewModel.getGlobalInfo()
        viewModel.getTrendCoins()
    }

    private fun registerWidgets() {
        bitcoinPriceChart = binding.bitcoinPriceChart
        dominancePieChart = binding.dominancePieChart
        trendCoinsRecycler = binding.trendCoinsRecycler

        initTrendRecyclerview()
        initBitcoinLineChart()
        initDominancePieChart()
    }

    private fun setObservables() {

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.bitcoinPriceInfoState.collect { result ->
                when (result) {
                    is Result.Empty -> {
//                        binding.progressBar.isVisible = false
                    }
                    is Result.Success -> {
//                        binding.progressBar.isVisible = false
                        updateBitcoinPriceInfo(result.data)
                    }
                    is Result.Error -> {
//                        binding.progressBar.isVisible = false
                        Timber.e("Bitcoin Price Info Error: ${result.message}")
                        context?.showToast(
                            message = "Error getting Bitcoin info: ${result.message}",
                            length = Toast.LENGTH_SHORT
                        )
                    }
                    is Result.Loading -> {
//                        binding.progressBar.isVisible = true
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.bitcoinChartInfoState.collect { result ->
                when (result) {
                    is Result.Empty -> {
                        binding.bitcoinProgressBar.isVisible = false
                    }
                    is Result.Success -> {
                        binding.bitcoinProgressBar.isVisible = false
                        updateBtcPriceChart(result.data)
                    }
                    is Result.Error -> {
                        binding.bitcoinProgressBar.isVisible = false
                        Timber.e("Bitcoin Chart Error: ${result.message}")
                        context?.showToast(
                            message = "Error getting Bitcoin Chart info: ${result.message}",
                            length = Toast.LENGTH_SHORT
                        )
                    }
                    is Result.Loading -> {
                        binding.bitcoinProgressBar.isVisible = true
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.marketInfoState.collect { result ->
                when (result) {
                    is Result.Empty -> {
                        binding.dominanceProgressBar.isVisible = false
                    }
                    is Result.Success -> {
                        binding.dominanceProgressBar.isVisible = false
                        updateDominanceChart(result.data)
                    }
                    is Result.Error -> {
                        binding.dominanceProgressBar.isVisible = false
                        Timber.e("Global Info Error: ${result.message}")
                        context?.showToast(
                            message = "Error getting global info: ${result.message}",
                            length = Toast.LENGTH_SHORT
                        )
                    }
                    is Result.Loading -> {
                        binding.dominanceProgressBar.isVisible = true
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.trendCoinsState.collect { result ->
                when (result) {
                    is Result.Empty -> {
                        binding.trendCoinsProgressBar.isVisible = false
                    }
                    is Result.Success -> {
                        binding.trendCoinsProgressBar.isVisible = false
                        updateTrendAdapter(result.data)
                    }
                    is Result.Error -> {
                        binding.trendCoinsProgressBar.isVisible = false
                        Timber.e("TrendCoins Error: ${result.message}")
                        context?.showToast(
                            message = "Error Getting Trend coins: ${result.message}",
                            length = Toast.LENGTH_SHORT
                        )
                    }
                    is Result.Loading -> {
                        binding.trendCoinsProgressBar.isVisible = true
                    }
                }
            }
        }
    }

    private fun initTrendRecyclerview() {

        trendsAdapter = TrendCoinsAdapter(onTrendCoinClicked)

        val llManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val dividerDrawable: Drawable? = context?.let { ctx ->
            ContextCompat.getDrawable(ctx, R.drawable.line_divider)
        }

        trendCoinsRecycler = binding.trendCoinsRecycler
        trendCoinsRecycler?.layoutManager = llManager


        val verticalBottomDecoration = DividerItemDecoration(
            trendCoinsRecycler?.context,
            llManager.orientation
        )
        dividerDrawable?.let {
            verticalBottomDecoration.setDrawable(it)
        }

//        trendCoinsRecycler?.addItemDecoration(verticalBottomDecoration)
        trendCoinsRecycler?.adapter = trendsAdapter
    }

    private fun updateTrendAdapter(coins: List<TrendCoin>) {
        viewLifecycleOwner.lifecycleScope.launch {
            trendsAdapter?.submitList(coins)
        }
    }

    private fun updateBitcoinPriceInfo(priceInfo: BitcoinPriceInfo) {
        val changePercentage: Double = priceInfo.info.usd24hChange ?: 0.0
        val price: Int = priceInfo.info.usd ?: 0
        val vol: Long = priceInfo.info.usd24hVol?.toLong() ?: 0L
        val cap: Long = priceInfo.info.usdMarketCap?.toLong() ?: 0L
        with(binding) {
            imgBitcoin.load(R.drawable.bitcoin_logo)
            txtBtcPrice.text = "$ " + price.decimalFormat()
            txtPricePercentChange.text = changePercentage.round(decimals = 2).toString() + "%"
            txtBtc24hVol.text = "vol: " + vol.decimalFormat()
            txtBtc24hCap.text = "cap: " + cap.decimalFormat()
        }
        applyMarketStateColor(changePercentage)
    }

    private fun applyMarketStateColor(change: Double) {
        val red: Int = context?.getColour(R.color.down_red) ?: Color.parseColor("#D32F2F")
        val green: Int = context?.getColour(R.color.up_green) ?: Color.parseColor("#00796B")

        when {
            change > 0 -> {
                binding.txtPricePercentChange.setTextColor(green)
                binding.imgUpDown.imageTintList = ColorStateList.valueOf(green)
                binding.imgUpDown.setImageResource(R.drawable.ic_arrow_drop_up)
            }
            change < 0 -> {
                binding.txtPricePercentChange.setTextColor(red)
                binding.imgUpDown.imageTintList = ColorStateList.valueOf(red)
                binding.imgUpDown.setImageResource(R.drawable.ic_arrow_drop_down)
            }
        }
    }

    private fun initBitcoinLineChart() {
        val firaSansLight = ResourcesCompat.getFont(requireContext(), R.font.firasans_light)

        bitcoinPriceChart?.apply {
            setBackgroundColor(Color.TRANSPARENT)
            setTouchEnabled(true)
            setDrawGridBackground(false)
            setPadding(0, 0, 0, 0)
            setViewPortOffsets(0f, 0f, 0f, 0f)

            // enable scaling and dragging
            isDragEnabled = true
            isHighlightPerDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            // modify the legend ...
            legend.apply {
                form = Legend.LegendForm.LINE
                textColor = Color.GRAY
                isWordWrapEnabled = true
                typeface = firaSansLight
                isEnabled = false
            }
            // modify description
            description.apply {
                textSize = 16.0f
                textColor = Color.LTGRAY
                text = "10 days chart"
                xOffset = 12.0f
                yOffset = 12.0f
                isEnabled = true
            }

            bitcoinPriceChart?.animateX(1000)
            axisRight?.isEnabled = false
            xAxis?.apply {
                position = XAxis.XAxisPosition.BOTTOM_INSIDE
                setAvoidFirstLastClipping(true)
                enableGridDashedLine(10f, 10f, 0f)
                setDrawGridLines(true)
                textColor = Color.GRAY
                typeface = firaSansLight
                isEnabled = false
            }
            axisLeft?.apply {
                enableGridDashedLine(10f, 10f, 0f)
                setDrawGridLines(true)
                isGranularityEnabled = true
                textColor = Color.GRAY
                spaceTop = 8.0f
//            spaceBottom = 8.0f
                setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)

            }
            data = LineData(
                listOf(
                    LineDataSet(listOf(Entry(0f, 0f)), "")
                )
            )
        }
    }

    private fun updateBtcPriceChart(entries: List<PriceEntry>) {
        if (entries.isNullOrEmpty()) {
            context?.showToast(message = "Not bitcoin data to show on chart")
            return
        }

        bitcoinPriceChart?.clear()
        val blue = Color.parseColor("#CD2962FF")
        val chartEntries: ArrayList<Entry> = ArrayList()
        entries.forEach { pe ->
            chartEntries.add(
                Entry(
                    pe.timeStamp.toFloat(),
                    pe.price.toFloat()
                )
            )
        }

        val datasetTitle: String = getString(R.string.btc_24_h_price_chart)
        val dataSet = LineDataSet(chartEntries, datasetTitle).apply {
            mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            cubicIntensity = 0.3f
            color = blue
            setDrawCircles(false)
            setDrawCircleHole(false)
            lineWidth = 2f
            formLineWidth = 1f
            formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            formSize = 13f
            valueTextSize = 9f
            enableDashedHighlightLine(10f, 5f, 0f)
            setDrawFilled(true)
            fillDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.gradient_blue)
            axisDependency = YAxis.AxisDependency.LEFT
        }
        val priceDataSets: ArrayList<ILineDataSet> = ArrayList()
        priceDataSets.add(dataSet)

        val lineData = LineData(priceDataSets).apply {
            setDrawValues(false)
            setValueTextSize(10f)
        }

        bitcoinPriceChart?.data = lineData
        bitcoinPriceChart?.data?.notifyDataChanged()
        bitcoinPriceChart?.notifyDataSetChanged()
        bitcoinPriceChart?.animateX(1000)
        bitcoinPriceChart?.animateY(700)
        bitcoinPriceChart?.invalidate()
    }

    private fun initDominancePieChart() {
        val firaSans = ResourcesCompat.getFont(requireContext(), R.font.firasans_regular)
        val firaSansLight = ResourcesCompat.getFont(requireContext(), R.font.firasans_light)
        dominancePieChart?.apply {
            setCenterTextTypeface(firaSans)
            setUsePercentValues(true)
            setDrawCenterText(true)
            setHoleColor(Color.TRANSPARENT)
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(110)
            isDrawHoleEnabled = true
            holeRadius = 60f
            transparentCircleRadius = 55f
            rotationAngle = 0f
            isRotationEnabled = true
            isHighlightPerTapEnabled = true
            dragDecelerationFrictionCoef = 0.8f
            setExtraOffsets(3f, 3f, 3f, 3f)
            animateY(1000, Easing.EaseInOutQuad)

            legend?.apply {
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                orientation = Legend.LegendOrientation.VERTICAL
                setDrawInside(false)
                isEnabled = false
                typeface = firaSansLight
                textSize = 10f
            }

            description?.apply {
                isEnabled = false
            }
            data = PieData(
                    PieDataSet(listOf(PieEntry(0f, 0f)), "")
            )
        }
    }

    private fun updateDominanceChart(marketInfo: GlobalMarketInfo) {
        marketInfo.marketCapPercentages?.let { caps ->

        }
        val marketCapPercentages: List<MarketCapPercentageItem> =
            marketInfo.marketCapPercentages ?: listOf()

        if (marketCapPercentages.isNullOrEmpty()) return

        dominancePieChart?.clear()

        var sum: Double = 0.0
        val chartValues: ArrayList<PieEntry> = ArrayList()
        marketCapPercentages.forEach { mcp ->
            sum += mcp.cap
            chartValues.add(
                PieEntry(
                    mcp.cap.toFloat(),
                    mcp.coinId
                )
            )
        }
        val othersDom: Double = 100 - sum
        chartValues.add(PieEntry(othersDom.toFloat(), "other"))

        val red = Color.parseColor("#A1F44336")
        val blue = Color.parseColor("#A12962FF")
        val lightBlue = Color.parseColor("#B5057CC5")
        val green = Color.parseColor("#A103B94D")
        val amber = Color.parseColor("#A1FFAB00")
        val yellow = Color.parseColor("#A1FFEB3B")
        val orange = Color.parseColor("#B2FF5722")
        val lightGreen = Color.parseColor("#B24CAF50")
        val pink = Color.parseColor("#B2E91E63")
        val graphPallet: ArrayList<Int> = ArrayList()
        chartValues.forEachIndexed { index, pieEntry ->
            when (index % 7) {
                0 -> graphPallet.add(red)
                1 -> graphPallet.add(blue)
                2 -> graphPallet.add(green)
                3 -> graphPallet.add(amber)
                4 -> graphPallet.add(yellow)
                5 -> graphPallet.add(orange)
                6 -> graphPallet.add(lightGreen)
                else -> graphPallet.add(pink)
            }
        }

        val dataSet = PieDataSet(chartValues, getString(R.string.market_dominance)).apply {
            sliceSpace = 3f
            selectionShift = 5f
            colors = graphPallet
//            setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
            yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        }

        val data = PieData(dataSet).apply {
            setValueFormatter(PercentFormatter())
            setValueTextSize(9f)
            setValueTextColor(Color.GRAY)
//          setValueTextColor(ColorUtils.fetchTxtColorPrimary(mContext))
//          setValueTypeface(iranSansNum)
        }

        dominancePieChart?.data = data
        dominancePieChart?.dragDecelerationFrictionCoef = 0.8f
        dominancePieChart?.animateY(1000, Easing.EaseInOutQuad)
        dominancePieChart?.data?.notifyDataChanged()
        dominancePieChart?.notifyDataSetChanged()
        dominancePieChart?.invalidate()
    }
}