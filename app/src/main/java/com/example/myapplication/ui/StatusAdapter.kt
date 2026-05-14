package com.example.myapplication.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.Station
import com.example.myapplication.databinding.ItemStatusBinding

class StatusAdapter(private val onStatusChanged: (Station, Boolean) -> Unit) :
    ListAdapter<Station, StatusAdapter.StatusViewHolder>(StationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        val binding = ItemStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StatusViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class StatusViewHolder(private val binding: ItemStatusBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(station: Station) {
            val context = binding.root.context
            binding.stationName.text = station.name
            binding.stationLine.text = context.getString(R.string.line_suffix, station.line)
            
            binding.statusSwitch.setOnCheckedChangeListener(null)
            binding.statusSwitch.isChecked = station.isActive
            binding.statusSwitch.text = if (station.isActive) 
                context.getString(R.string.station_available) 
            else 
                context.getString(R.string.station_unavailable)
            
            binding.statusSwitch.setOnCheckedChangeListener { _, isChecked ->
                onStatusChanged(station, isChecked)
            }
        }
    }

    class StationDiffCallback : DiffUtil.ItemCallback<Station>() {
        override fun areItemsTheSame(oldItem: Station, newItem: Station): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Station, newItem: Station): Boolean {
            return oldItem == newItem
        }
    }
}
