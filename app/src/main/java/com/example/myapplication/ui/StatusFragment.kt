package com.example.myapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.MetroApplication
import com.example.myapplication.databinding.FragmentStatusBinding
import com.example.myapplication.viewmodel.MetroViewModel
import com.example.myapplication.viewmodel.MetroViewModelFactory

class StatusFragment : Fragment() {

    private var _binding: FragmentStatusBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MetroViewModel by viewModels {
        MetroViewModelFactory((requireActivity().application as MetroApplication).repository)
    }

    private lateinit var adapter: StatusAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        viewModel.loadStations()
    }

    private fun setupRecyclerView() {
        adapter = StatusAdapter { station, isActive ->
            viewModel.toggleStationStatus(station, isActive)
        }
        binding.statusRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.statusRecyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.stations.observe(viewLifecycleOwner) { stations ->
            adapter.submitList(stations)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
