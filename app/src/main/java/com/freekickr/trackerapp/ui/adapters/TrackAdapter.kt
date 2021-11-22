package com.freekickr.trackerapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.freekickr.trackerapp.database.entities.Track
import com.freekickr.trackerapp.databinding.ActivityMainBinding
import com.freekickr.trackerapp.databinding.ItemTrackBinding
import com.freekickr.trackerapp.utils.TimerConverter
import java.text.SimpleDateFormat
import java.util.*

class TrackAdapter : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Track>() {
        override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun updateList(list: List<Track>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = differ.currentList[position]
        holder.bind(track)
    }

    override fun getItemCount() = differ.currentList.size

    inner class TrackViewHolder(private val binding: ItemTrackBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(track: Track) {
                Glide.with(binding.root).load(track.img).into(binding.ivRunImage)

                val calendar = Calendar.getInstance().apply {
                    timeInMillis = track.timestamp
                }

                val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
                binding.tvDate.text = dateFormat.format(calendar.time)

                val avgSpeed = "${track.avgSpeed}km/h"
                binding.tvAvgSpeed.text = avgSpeed

                val distance = "${track.distance / 1000f}km"
                binding.tvDistance.text = distance

                binding.tvTime.text = TimerConverter.getFormattedStopWatchTime(track.elapsedTime)

                val calories = "${track.calories}cal"
                binding.tvCalories.text = calories
            }
    }
}