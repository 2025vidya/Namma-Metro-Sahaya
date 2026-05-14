package com.example.myapplication.viewmodel

import androidx.lifecycle.*
import com.example.myapplication.data.Station
import com.example.myapplication.repository.MetroRepository
import kotlinx.coroutines.launch

class MetroViewModel(private val repository: MetroRepository) : ViewModel() {

    private val _stations = MutableLiveData<List<Station>>()
    val stations: LiveData<List<Station>> = _stations

    private val _route = MutableLiveData<List<Station>>()
    val route: LiveData<List<Station>> = _route

    private val _totalFare = MutableLiveData<Int>()
    val totalFare: LiveData<Int> = _totalFare

    private val _totalTime = MutableLiveData<Int>()
    val totalTime: LiveData<Int> = _totalTime

    fun loadStations() {
        viewModelScope.launch {
            repository.initData()
            _stations.value = repository.getAllStations()
        }
    }

    fun findRoute(fromName: String, toName: String) {
        viewModelScope.launch {
            val fromStation = repository.getStationByName(fromName)
            val toStation = repository.getStationByName(toName)

            if (fromStation != null && toStation != null) {
                val path = repository.findShortestPath(fromStation.id, toStation.id)
                _route.value = path
                calculateFareAndTime(path)
            }
        }
    }

    fun toggleStationStatus(station: Station, isActive: Boolean) {
        viewModelScope.launch {
            val updatedStation = station.copy(isActive = isActive)
            repository.updateStation(updatedStation)
            _stations.value = repository.getAllStations()
        }
    }

    private fun calculateFareAndTime(path: List<Station>) {
        if (path.size < 2) {
            _totalFare.value = 0
            _totalTime.value = 0
            return
        }
        // Simple calculation: ₹10 per hop, 5 mins per hop
        _totalFare.value = (path.size - 1) * 10
        _totalTime.value = (path.size - 1) * 5
    }
}

class MetroViewModelFactory(private val repository: MetroRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MetroViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MetroViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
