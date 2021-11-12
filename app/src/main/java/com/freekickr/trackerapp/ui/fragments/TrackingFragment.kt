package com.freekickr.trackerapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.freekickr.trackerapp.R
import com.freekickr.trackerapp.databinding.FragmentTrackingBinding
import com.freekickr.trackerapp.services.Polyline
import com.freekickr.trackerapp.services.TrackingService
import com.freekickr.trackerapp.ui.viewmodels.MainViewModel
import com.freekickr.trackerapp.utils.Constants.ACTION_PAUSE_SERVICE
import com.freekickr.trackerapp.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import com.freekickr.trackerapp.utils.Constants.MAP_ZOOM
import com.freekickr.trackerapp.utils.Constants.POLYLINE_COLOR
import com.freekickr.trackerapp.utils.Constants.POLYLINE_WIDTH
import com.freekickr.trackerapp.utils.TimerConverter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackingFragment: Fragment() {

    private lateinit var binding: FragmentTrackingBinding

    private val viewModel: MainViewModel by viewModels()

    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()

    private var map: GoogleMap? = null

    private var currentTimeInMillis = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrackingBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mvMap.onCreate(savedInstanceState)

        binding.fabStartTracking.setOnClickListener {
            toggleRun()
        }

        binding.mvMap.getMapAsync {
            map = it
            addAllPolylines()
        }

        subscribeToService()
    }

    private fun subscribeToService() {
        TrackingService.isTracking.observe(viewLifecycleOwner, {
            updateTracking(it)
        })

        TrackingService.pathPoints.observe(viewLifecycleOwner, {
            pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
        })

        TrackingService.timeRunInMillis.observe(viewLifecycleOwner, {
            currentTimeInMillis = it
            val formattedString = TimerConverter.getFormattedStopWatchTime(currentTimeInMillis, true)
            binding.tvTimer.text = formattedString
        })
    }

    private fun toggleRun() {
        if (isTracking) {
            sendCommandToService(ACTION_PAUSE_SERVICE)
        } else {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if (!isTracking) {
            binding.fabStartTracking.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_gradient)
            //TODO change state of start button
        } else {
            binding.fabStartTracking.background = ContextCompat.getDrawable(requireContext(), R.drawable.icon_splash)
        }
    }

    private fun moveCameraToUser() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(pathPoints.last().last(), MAP_ZOOM)
            )
        }
    }

    private fun addAllPolylines() {
        pathPoints.forEach {
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(it)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun addLatestPolyline() {
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLng = pathPoints.last().last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun sendCommandToService(action: String): Intent {
        return Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mvMap?.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mvMap?.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mvMap?.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.mvMap?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mvMap?.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mvMap?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mvMap?.onSaveInstanceState(outState)
    }

}