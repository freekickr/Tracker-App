package com.freekickr.trackerapp.domain.marker_view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.freekickr.trackerapp.R
import com.freekickr.trackerapp.database.entities.Track
import com.freekickr.trackerapp.utils.TimerConverter
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.*

class CustomMarkerView(
    context: Context,
    val tracks: List<Track>,
    layoutId: Int
): MarkerView(context, layoutId) {

    override fun getOffset(): MPPointF {
        return MPPointF(-width / 2f, -height.toFloat())
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)
        e?.let {
            val currentTrackId = it.x.toInt()
            val track = tracks[currentTrackId]

            val calendar = Calendar.getInstance().apply {
                timeInMillis = track.timestamp
            }

            val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            findViewById<TextView>(R.id.tvDate).text = dateFormat.format(calendar.time)

            val avgSpeed = "${track.avgSpeed}km/h"
            findViewById<TextView>(R.id.tvAvgSpeed).text = avgSpeed

            val distance = "${track.distance / 1000f}km"
            findViewById<TextView>(R.id.tvDistance).text = distance

            findViewById<TextView>(R.id.tvDuration).text = TimerConverter.getFormattedStopWatchTime(track.elapsedTime)

            val calories = "${track.calories}cal"
            findViewById<TextView>(R.id.tvCaloriesBurned).text = calories
        }
    }

}