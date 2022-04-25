package com.mrostami.geckoin.presentation.price_chart

import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.mrostami.geckoin.R
import com.mrostami.geckoin.databinding.PriceChartFragmentBinding
import com.mrostami.geckoin.domain.base.Result
import com.mrostami.geckoin.model.PriceEntry
import com.mrostami.geckoin.presentation.utils.showToast
import com.mrostami.geckoin.presentation.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class PriceChartFragment() : Fragment(R.layout.price_chart_fragment) {

    constructor(_coinId: String) : this() {
        this.coinId = _coinId
    }

    val viewModel: PriceChartViewModel by viewModels()
    private val binding: PriceChartFragmentBinding by viewBinding(PriceChartFragmentBinding::bind)
    private var priceChart: LineChart? = null
    private var coinId: String? = null

    companion object {
        const val FRAG_TAG = "coin_price_chart_fragment"
        fun newInstance(coinId: String): PriceChartFragment {
            return PriceChartFragment(coinId)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerWidgets()
        setObservers()
        requestForData()
    }

    private fun registerWidgets() {
        priceChart = binding.priceChart
        initLineChart()
    }

    private fun setObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.priceChartInfoStateFlow.collectLatest { result ->
                    when (result) {
                        is Result.Empty -> {
                            binding.progressBar.isVisible = false
                        }
                        is Result.Success -> {
                            binding.progressBar.isVisible = false
                            updateBtcPriceChart(result.data)
                        }
                        is Result.Error -> {
                            binding.progressBar.isVisible = false
                            Timber.e("Price Chart Error: ${result.message}")
                            context?.showToast(
                                message = "Error getting Price Chart info: ${result.message}",
                                length = Toast.LENGTH_SHORT
                            )
                        }
                        is Result.Loading -> {
                            binding.progressBar.isVisible = true
                        }
                    }
                }
            }
        }
    }

    private fun requestForData() {
        coinId?.let { id ->
            viewModel.getPriceChartInfo(id)
        }
    }

    private fun initLineChart() {
        val firaSansLight = ResourcesCompat.getFont(requireContext(), R.font.firasans_light)

        priceChart?.apply {
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

            priceChart?.animateX(1000)
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

        priceChart?.clear()
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

        priceChart?.run {
            data = lineData
            data?.notifyDataChanged()
            notifyDataSetChanged()
            animateX(1000)
            animateY(700)
            invalidate()
        }
    }
}