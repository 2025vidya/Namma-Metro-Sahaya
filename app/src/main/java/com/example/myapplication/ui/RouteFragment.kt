package com.example.myapplication.ui

import android.graphics.Color
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.MetroApplication
import com.example.myapplication.R
import com.example.myapplication.data.RouteStep
import com.example.myapplication.data.Station
import com.example.myapplication.databinding.FragmentRouteBinding
import com.example.myapplication.viewmodel.MetroViewModel
import com.example.myapplication.viewmodel.MetroViewModelFactory
import java.util.*

/**
 * Fragment that displays the step-by-step route guidance.
 * It transforms the list of metro stations into a user-friendly visual guide
 * including ticket purchase and gate usage instructions.
 */
class RouteFragment : Fragment(), TextToSpeech.OnInitListener {

    private var _binding: FragmentRouteBinding? = null
    private val binding get() = _binding!!

    private val args: RouteFragmentArgs by navArgs()
    private val viewModel: MetroViewModel by viewModels {
        MetroViewModelFactory((requireActivity().application as MetroApplication).repository)
    }

    private lateinit var adapter: RouteAdapter
    private var tts: TextToSpeech? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRouteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tts = TextToSpeech(requireContext(), this)
        setupRecyclerView()
        observeViewModel()

        viewModel.findRoute(args.startStation, args.endStation)

        binding.speakButton.setOnClickListener {
            speakInstructions()
        }
    }

    private fun setupRecyclerView() {
        adapter = RouteAdapter()
        binding.routeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.routeRecyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.route.observe(viewLifecycleOwner) { stations ->
            if (stations.isNotEmpty()) {
                val steps = generateRouteSteps(stations)
                adapter.submitList(steps)
            }
        }

        viewModel.totalFare.observe(viewLifecycleOwner) { fare ->
            binding.fareText.text = getString(R.string.fare_est, fare)
        }

        viewModel.totalTime.observe(viewLifecycleOwner) { time ->
            binding.timeText.text = getString(R.string.time_est, time)
        }
    }

    /**
     * Converts a list of stations into a comprehensive guide with visual steps.
     */
    private fun generateRouteSteps(stations: List<Station>): List<RouteStep> {
        val steps = mutableListOf<RouteStep>()

        // 1. Purchase Ticket
        steps.add(RouteStep(
            title = "Ticket Counter",
            titleKn = "ಟಿಕೆಟ್ ಕೌಂಟರ್",
            instruction = getString(R.string.step_purchase),
            iconRes = R.drawable.ic_ticket_machine
        ))

        // 2. Entry Gate
        steps.add(RouteStep(
            title = "Entry Gate",
            titleKn = "ಪ್ರವೇಶ ದ್ವಾರ",
            instruction = getString(R.string.step_tap_entry),
            iconRes = R.drawable.ic_metro_gate
        ))

        // 3. Platform & Stations
        stations.forEachIndexed { index, station ->
            val color = if (station.line == "Purple") Color.parseColor("#7E57C2") else Color.parseColor("#43A047")
            
            val instruction = when {
                index == 0 -> getString(R.string.go_to_platform, station.platformInfo)
                station.isInterchange -> getString(R.string.interchange_alert)
                index == stations.size - 1 -> getString(R.string.arrival_at, station.name, station.exitInfo, station.exitInfo)
                else -> getString(R.string.stay_on_train, stations[index + 1].name)
            }

            steps.add(RouteStep(
                title = station.name,
                titleKn = station.nameKn,
                instruction = instruction,
                iconRes = if (index == 0) R.drawable.ic_metro_platform else android.R.drawable.ic_dialog_map,
                color = color,
                isInterchange = station.isInterchange
            ))
        }

        // 4. Exit Gate
        steps.add(RouteStep(
            title = "Exit Gate",
            titleKn = "ನಿರ್ಗಮನ ದ್ವಾರ",
            instruction = getString(R.string.step_tap_exit),
            iconRes = R.drawable.ic_metro_gate
        ))

        return steps
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("kn", "IN"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.US)
            }
        }
    }

    private fun speakInstructions() {
        val steps = adapter.getSteps()
        if (steps.isEmpty()) return
        
        val sb = StringBuilder()
        steps.forEach { step ->
            sb.append(step.instruction).append(". ")
        }
        tts?.speak(sb.toString(), TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tts?.stop()
        tts?.shutdown()
        _binding = null
    }
}
