package com.freekickr.trackerapp.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.freekickr.trackerapp.R
import com.freekickr.trackerapp.databinding.FragmentStatsBinding
import com.freekickr.trackerapp.domain.marker_view.CustomMarkerView
import com.freekickr.trackerapp.ui.viewmodels.StatsViewModel
import com.freekickr.trackerapp.utils.TimerConverter
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.round

@AndroidEntryPoint
class StatsFragment: Fragment() {

    private lateinit var binding: FragmentStatsBinding

    private val viewModel: StatsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatsBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        setupBarChart()
    }

    private fun subscribeToObservers() {
        viewModel.totalTimeRun.observe(viewLifecycleOwner, {
            it?.let {
                val totalTimeRun = TimerConverter.getFormattedStopWatchTime(it)
                binding.tvTotalTime.text = totalTimeRun
            }
        })
        viewModel.totalDistance.observe(viewLifecycleOwner, {
            it?.let {
                val km = it / 1000f
                val total = round(km * 10f) / 10f
                val totalDistance = "${total}km"
                binding.tvTotalDistance.text = totalDistance
            }
        })
        viewModel.avgSpeed.observe(viewLifecycleOwner, {
            it?.let {
                val avgSpeed = round(it * 10f) / 10f
                val avgSpeedRes = "${avgSpeed}km/h"
                binding.tvAverageSpeed.text = avgSpeedRes
            }
        })
        viewModel.totalCalories.observe(viewLifecycleOwner, {
            it?.let {
                val calories = "${it}kcal"
                binding.tvTotalCalories.text = calories
            }
        })
        viewModel.runsSortedByDate.observe(viewLifecycleOwner, {
            it?.let {
                val allAvgSpeeds = it.indices.map { i -> BarEntry(i.toFloat(), it[i].avgSpeed) }
                val barDataSet = BarDataSet(allAvgSpeeds, "Avg speed over time").apply {
                    valueTextColor = Color.YELLOW
                    color = ContextCompat.getColor(requireContext(), R.color.teal_200)
                }
                binding.barChart.data = BarData(barDataSet)
                binding.barChart.marker = CustomMarkerView(requireContext(), it, R.layout.marker_view)
                binding.barChart.invalidate()
            }
        })
    }

    private fun setupBarChart() {
        binding.barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            axisLineColor = Color.GREEN
            textColor = Color.BLACK
            setDrawGridLines(false)
        }
        binding.barChart.axisLeft.apply {
            axisLineColor = Color.GREEN
            textColor = Color.BLACK
            setDrawGridLines(false)
        }
        binding.barChart.axisRight.apply {
            axisLineColor = Color.GREEN
            textColor = Color.BLACK
            setDrawGridLines(false)
        }
        binding.barChart.apply {
            description.text = "Avg speed over time"
            legend.isEnabled = false
        }
    }

}