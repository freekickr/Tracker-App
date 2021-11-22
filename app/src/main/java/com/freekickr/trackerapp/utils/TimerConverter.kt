package com.freekickr.trackerapp.utils

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.concurrent.TimeUnit

object TimerConverter {

    fun getFormattedStopWatchTime(ms: Long, includeMillis: Boolean = false): String {
        var millis = ms
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        millis -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
        millis -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis)

        val f: NumberFormat = DecimalFormat("00")

        return if (includeMillis) {
            millis -= TimeUnit.SECONDS.toMillis(seconds)
            "${f.format(hours)}:${f.format(minutes)}:${f.format(seconds)}:${f.format(millis)}"
        } else {
            "${f.format(hours)}:${f.format(minutes)}:${f.format(seconds)}"
        }
    }

}