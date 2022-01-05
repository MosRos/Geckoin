package com.mrostami.geckoin.presentation.home

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.math.MathUtils
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.mrostami.geckoin.R
import com.mrostami.geckoin.databinding.HomeFragmentBinding
import com.mrostami.geckoin.domain.base.Result
import com.mrostami.geckoin.model.BitcoinPriceInfo
import com.mrostami.geckoin.model.GlobalMarketInfo
import com.mrostami.geckoin.model.MarketCapPercentageItem
import com.mrostami.geckoin.model.PriceEntry
import com.mrostami.geckoin.presentation.utils.round
import com.mrostami.geckoin.presentation.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import java.text.DecimalFormat
import java.text.NumberFormat

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

        initBitcoinLineChart()
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
                        updateBtcPriceChart(result.data)
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
                        updateDominanceChart(result.data)
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
        val df: DecimalFormat = DecimalFormat("###,###,###,###")
        binding.imgBitcoin.load(R.drawable.bitcoin_logo)
        val changePercentage: Double = priceInfo.info.usd24hChange ?: 0.0
        val price: Int = priceInfo.info.usd ?: 0
        val vol: Long = priceInfo.info.usd24hVol?.toLong() ?: 0L
        val cap: Long = priceInfo.info.usdMarketCap?.toLong() ?: 0L
        binding.txtBtcPrice.text = df.format(price).toString() + "  usd"
        binding.txtPricePercentChange.text = changePercentage.round(decimals = 2).toString()
        binding.txtBtc24hVol.text = "vol: " + df.format(vol).toString()
        binding.txtBtc24hCap.text = "cap: " + df.format(cap).toString()

        when {

            changePercentage > 0 -> {

                binding.txtBtcPrice.setTextColor(ContextCompat.getColor(requireContext(), R.color.up_green))
                binding.txtPricePercentChange.setTextColor(ContextCompat.getColor(requireContext(), R.color.up_green))
                binding.imgUpDown.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.up_green))
                binding.imgUpDown.setImageResource(R.drawable.ic_arrow_drop_up)
            }
            changePercentage < 0 -> {
                binding.txtBtcPrice.setTextColor(ContextCompat.getColor(requireContext(), R.color.down_red))
                binding.txtPricePercentChange.setTextColor(ContextCompat.getColor(requireContext(), R.color.down_red))
                binding.imgUpDown.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.down_red))
                binding.imgUpDown.setImageResource(R.drawable.ic_arrow_drop_down)
            }
        }
    }

    private fun initBitcoinLineChart(){
        val firaSansLight = ResourcesCompat.getFont(requireContext(), R.font.firasans_light)

        bitcoinPriceChart?.apply {
            setBackgroundColor(Color.TRANSPARENT)
            description.isEnabled = false
            setTouchEnabled(true)
            setDrawGridBackground(false)
            setPadding(0, 0, 0, 0)
            setViewPortOffsets(0f, 0f, 0f, 0f)

            // enable scaling and dragging
            isDragEnabled = true
            setScaleEnabled(true)
            isHighlightPerDragEnabled = true
//            scaleY = 0.9f
            // force pinch zoom along both axis
            setPinchZoom(true)

            legend.typeface = firaSansLight
            legend.isEnabled = false

            // modify the legend ...
            legend.form = Legend.LegendForm.LINE
            legend.textColor = Color.GRAY
            legend.isWordWrapEnabled = true

            description.isEnabled = false
            axisRight?.isEnabled = false
        }


        val xAxis = bitcoinPriceChart?.xAxis?.apply {
            position = XAxis.XAxisPosition.BOTTOM_INSIDE
            setAvoidFirstLastClipping(true)
            enableGridDashedLine(10f, 10f, 0f)
            setDrawGridLines(false)
            typeface = firaSansLight
            isEnabled = false
        }

        val leftAxis = bitcoinPriceChart?.axisLeft?.apply {
            enableGridDashedLine(10f, 10f, 0f)
            setDrawGridLines(false)
            isGranularityEnabled = true

//        leftAxis.valueFormatter = LargeValueFormatter()
//        leftAxis.axisMinimum = 0f // this replaces setStartAtZero(true)
            spaceTop = 8.0f
//            spaceBottom = 8.0f
            setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)

        }

        bitcoinPriceChart?.animateX(1000)

    }

    private fun updateBtcPriceChart(entries: List<PriceEntry>) {
        if (entries.isNullOrEmpty()){
            Toast.makeText(requireContext(), "Not bitcoin data to show on chart", Toast.LENGTH_LONG).show()
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
        val dataSet = LineDataSet(chartEntries, datasetTitle)
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.color = blue
        dataSet.setDrawCircles(false)
        dataSet.setDrawCircleHole(false)
        dataSet.lineWidth = 2f

        // customize legend entry
        dataSet.formLineWidth = 1f
        dataSet.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
        dataSet.formSize = 13f
        // text size of values
        dataSet.valueTextSize = 9f
//        dataSet.valueTextColor = ColorUtils.fetchTxtColorPrimary(mContext)
        // draw selection line as dashed
        dataSet.enableDashedHighlightLine(10f, 5f, 0f)

        dataSet.setDrawFilled(true)
//        dataSet.fillFormatter = IFillFormatter { dataSet, dataProvider ->
//            bitcoinPriceChart?.axisLeft?.axisMinimum
//        }

        dataSet.fillDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.gradiant1)

        dataSet.axisDependency = YAxis.AxisDependency.LEFT
        val priceDataSets: ArrayList<ILineDataSet> = ArrayList()
        priceDataSets.add(dataSet)

        val lineData = LineData(priceDataSets)
        lineData.setDrawValues(false)
        lineData.setValueTextSize(10f)
//        lineData.setValueTypeface(iranSansNum)

        bitcoinPriceChart?.data = lineData
        bitcoinPriceChart?.data?.notifyDataChanged()
        bitcoinPriceChart?.notifyDataSetChanged()
        bitcoinPriceChart?.animateX(1200)
        bitcoinPriceChart?.animateY(1200)

        bitcoinPriceChart?.invalidate()
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
            holeRadius = 60f
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
        val marketCapPercentages: List<MarketCapPercentageItem> = marketInfo.marketCapPercentages ?: listOf()

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

        val dataSet = PieDataSet(chartValues, getString(R.string.market_dominances))
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f

        val red = Color.parseColor("#A1F44336")
        val blue = Color.parseColor("#A12962FF")
        val lightBlue = Color.parseColor("#B5057CC5")
        val green = Color.parseColor("#A103B94D")
        val amber = Color.parseColor("#A1FFAB00")
        val yellow = Color.parseColor("#A1FFEB3B")
        val orange = Color.parseColor("#B2FF5722")
        val lightGreen = Color.parseColor("#B24CAF50")
        val pink = Color.parseColor("#B2E91E63")

        val graphPallete: ArrayList<Int> = ArrayList()
        chartValues.forEachIndexed { index, pieEntry ->
            when(index%7) {
                0 -> graphPallete.add(red)
                1 -> graphPallete.add(blue)
                2 -> graphPallete.add(green)
                3 -> graphPallete.add(amber)
                4 -> graphPallete.add(yellow)
                5 -> graphPallete.add(orange)
                6 -> graphPallete.add(lightGreen)
                else -> graphPallete.add(pink)
            }
        }

        dataSet.colors = graphPallete

        //dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(9f)

//        data.setValueTextColor(ColorUtils.fetchTxtColorPrimary(mContext))
//        data.setValueTypeface(iranSansNum)

        dominancePieChart?.data = data

        dominancePieChart?.dragDecelerationFrictionCoef = 0.8f
        dominancePieChart?.animateY(1000, Easing.EaseInOutQuad)

        dominancePieChart?.data?.notifyDataChanged()
        dominancePieChart?.notifyDataSetChanged()

        dominancePieChart?.invalidate()
    }
}