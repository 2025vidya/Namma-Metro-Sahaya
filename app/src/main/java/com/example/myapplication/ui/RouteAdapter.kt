package com.example.myapplication.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.RouteStep
import com.example.myapplication.databinding.ItemStationBinding

/**
 * Adapter for displaying the step-by-step metro guide.
 * It uses RouteStep which can represent stations or specific actions (like buying a ticket).
 */
class RouteAdapter : RecyclerView.Adapter<RouteAdapter.RouteViewHolder>() {

    private var steps: List<RouteStep> = emptyList()

    fun submitList(newSteps: List<RouteStep>) {
        steps = newSteps
        notifyDataSetChanged()
    }

    fun getSteps(): List<RouteStep> = steps

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemStationBinding.inflate(layoutInflater, parent, false)
        return RouteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        val step = steps[position]
        holder.bind(step)
        
        // Simple animation for better UX as items appear
        holder.itemView.startAnimation(
            AnimationUtils.loadAnimation(holder.itemView.context, R.anim.fade_in)
        )
    }

    override fun getItemCount(): Int = steps.size

    class RouteViewHolder(private val binding: ItemStationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(step: RouteStep) {
            binding.stationName.text = step.title
            // Use titleKn if available for Kannada support
            binding.stationNameKn.text = step.titleKn ?: ""
            binding.instructionText.text = step.instruction
            binding.stationIcon.setImageResource(step.iconRes)
            
            // Set line indicator color if provided
            if (step.color != null) {
                binding.lineIndicator.setBackgroundColor(step.color)
                binding.lineIndicator.visibility = android.view.View.VISIBLE
            } else {
                binding.lineIndicator.visibility = android.view.View.INVISIBLE
            }

            // Highlight interchange or special steps
            if (step.isInterchange) {
                binding.root.setCardBackgroundColor(Color.parseColor("#FFF9C4")) // Light yellow
            } else {
                binding.root.setCardBackgroundColor(Color.WHITE)
            }
        }
    }
}
