package com.example.myapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myapplication.MetroApplication
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.viewmodel.MetroViewModel
import com.example.myapplication.viewmodel.MetroViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MetroViewModel by viewModels {
        MetroViewModelFactory((requireActivity().application as MetroApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadStations()

        viewModel.stations.observe(viewLifecycleOwner) { stations ->
            val activeStations = stations.filter { it.isActive }
            val stationNames = activeStations.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, stationNames)
            binding.fromStationAutoComplete.setAdapter(adapter)
            binding.toStationAutoComplete.setAdapter(adapter)
        }

        binding.findRouteButton.setOnClickListener {
            val from = binding.fromStationAutoComplete.text.toString()
            val to = binding.toStationAutoComplete.text.toString()

            if (from.isNotEmpty() && to.isNotEmpty()) {
                val action = HomeFragmentDirections.actionHomeFragmentToRouteFragment(from, to)
                findNavController().navigate(action)
            }
        }

        binding.stationStatusButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_statusFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
