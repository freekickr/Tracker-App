package com.freekickr.trackerapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.freekickr.trackerapp.R
import com.freekickr.trackerapp.database.entities.Track
import com.freekickr.trackerapp.databinding.FragmentTrackingBinding
import com.freekickr.trackerapp.domain.User
import com.freekickr.trackerapp.services.Polyline
import com.freekickr.trackerapp.services.TrackingService
import com.freekickr.trackerapp.ui.viewmodels.TrackingViewModel
import com.freekickr.trackerapp.utils.Constants.ACTION_PAUSE_SERVICE
import com.freekickr.trackerapp.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import com.freekickr.trackerapp.utils.Constants.ACTION_STOP_SERVICE
import com.freekickr.trackerapp.utils.Constants.MAP_ZOOM
import com.freekickr.trackerapp.utils.Constants.POLYLINE_COLOR
import com.freekickr.trackerapp.utils.Constants.POLYLINE_WIDTH
import com.freekickr.trackerapp.utils.DistanceFinder
import com.freekickr.trackerapp.utils.TimerConverter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import kotlin.math.round

@AndroidEntryPoint
class TrackingFragment : Fragment() {

    private lateinit var binding: FragmentTrackingBinding

    private val viewModel: TrackingViewModel by viewModels()

    private var isTracking: MutableLiveData<Boolean> = MutableLiveData(false)
    private var pathPoints = mutableListOf<Polyline>()

    private var map: GoogleMap? = null

    private var currentTimeInMillis = 0L

    val weight: Float by lazy {
        viewModel.getUserWeight()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrackingBinding.inflate(layoutInflater)


        setButtonsListeners()

        observeTrackingState()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mvMap.onCreate(savedInstanceState)

        binding.mvMap.getMapAsync {
            map = it
            addAllPolylines()
        }

        subscribeToService()
    }

    private fun observeTrackingState() {
        isTracking.observe(viewLifecycleOwner, {
            Log.d("TAG", "observeTrackingState: $currentTimeInMillis $it")
            if (!it && currentTimeInMillis == 0L) {
                binding.fabStartTracking.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_baseline_play_arrow_24
                    )
                )
            } else if (!it && currentTimeInMillis > 0L) {
                showAdditionalButtons()
                binding.fabStartTracking.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_baseline_play_arrow_24
                    )
                )
            } else {
                binding.fabStartTracking.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_baseline_pause_24
                    )
                )
                hideAdditionalButtons()
            }
        })
    }

    private fun showAdditionalButtons() {
        binding.fabCancelTrack.visibility = View.VISIBLE
        binding.fabFinishTrack.visibility = View.VISIBLE
    }

    private fun hideAdditionalButtons() {
        binding.fabCancelTrack.visibility = View.INVISIBLE
        binding.fabFinishTrack.visibility = View.INVISIBLE
    }

    private fun setButtonsListeners() {
        binding.fabStartTracking.setOnClickListener {
            toggleRun()
        }

        binding.fabFinishTrack.setOnClickListener {
            zoomToSeeWholeTrack()
            endRunAndSaveToDb()
        }

        binding.fabCancelTrack.setOnClickListener {
            showCancelTrackingDialog()
        }
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
            val formattedString =
                TimerConverter.getFormattedStopWatchTime(currentTimeInMillis, true)
            binding.tvTimer.text = formattedString
        })
    }

    private fun showCancelTrackingDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Cancel the track?")
            .setMessage("Are you sure to cancel the current track and delete all it's data?")
            .setIcon(R.drawable.ic_baseline_delete_24)
            .setPositiveButton("Yes") { _, _ ->
                stopTrack()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
            .create()
        dialog.show()
    }

    private fun stopTrack() {
        currentTimeInMillis = 0L
        pathPoints.clear()
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigateUp()
    }

    private fun toggleRun() {
        isTracking.value?.let {
            if (it) {
                sendCommandToService(ACTION_PAUSE_SERVICE)
            } else {
                sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
            }
        }
    }

    private fun updateTracking(isTracking: Boolean) {
        this.isTracking.postValue(isTracking)
    }

    private fun moveCameraToUser() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(pathPoints.last().last(), MAP_ZOOM)
            )
        }
    }

    private fun zoomToSeeWholeTrack() {
        val bounds = LatLngBounds.Builder()
        pathPoints.forEach { polyline ->
            polyline.forEach { position ->
                bounds.include(position)
            }
        }

        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                binding.mvMap.width,
                binding.mvMap.height,
                (binding.mvMap.height * 0.05f).toInt()
            )
        )
    }

    private fun endRunAndSaveToDb() {
        map?.snapshot { bmp ->
            var distanceInMeters = 0
            pathPoints.forEach {
                distanceInMeters += DistanceFinder.calculatePolylineLength(it).toInt()
            }

            val avgSpeed =
                round((distanceInMeters / 1000f) / (currentTimeInMillis / 1000f / 60 / 60) * 10) / 10f
            val dateTimestamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMeters / 1000f) * weight).toInt()

            val track = Track(
                img = bmp,
                avgSpeed = avgSpeed,
                distance = distanceInMeters,
                elapsedTime = currentTimeInMillis,
                calories = caloriesBurned,
                timestamp = dateTimestamp
            )

            viewModel.insertTrack(track)
            Snackbar.make(
                requireActivity().findViewById(R.id.rootView),
                "Track saved successfully",
                Snackbar.LENGTH_LONG
            ).apply {
                anchorView = requireActivity().findViewById(R.id.bottomNavigationView)
                show()
            }
            stopTrack()
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