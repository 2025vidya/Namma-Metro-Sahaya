package com.example.myapplication.repository

import com.example.myapplication.data.MetroDao
import com.example.myapplication.data.Station
import com.example.myapplication.data.Connection
import java.util.*

class MetroRepository(private val metroDao: MetroDao) {

    suspend fun getAllStations() = metroDao.getAllStations()
    suspend fun getStationByName(name: String) = metroDao.getStationByName(name)

    suspend fun updateStation(station: Station) {
        metroDao.updateStation(station)
    }

    /**
     * Dijkstra's algorithm to find the shortest path between two stations.
     * Considers only active stations.
     */
    suspend fun findShortestPath(startStationId: Int, endStationId: Int): List<Station> {
        val allStations = metroDao.getAllStations().associateBy { it.id }
        val allConnections = metroDao.getAllConnections()

        val adjacencyList = mutableMapOf<Int, MutableList<Pair<Int, Int>>>()
        allConnections.forEach { conn ->
            val from = allStations[conn.fromStationId]
            val to = allStations[conn.toStationId]
            
            if (from?.isActive == true && to?.isActive == true) {
                adjacencyList.computeIfAbsent(conn.fromStationId) { mutableListOf() }.add(conn.toStationId to conn.weight)
                adjacencyList.computeIfAbsent(conn.toStationId) { mutableListOf() }.add(conn.fromStationId to conn.weight)
            }
        }

        val distances = mutableMapOf<Int, Int>().withDefault { Int.MAX_VALUE }
        val previous = mutableMapOf<Int, Int?>()
        val priorityQueue = PriorityQueue<Pair<Int, Int>>(compareBy { it.second })

        distances[startStationId] = 0
        priorityQueue.add(startStationId to 0)

        val visited = mutableSetOf<Int>()

        while (priorityQueue.isNotEmpty()) {
            val (currentId, currentDist) = priorityQueue.poll()!!

            if (currentId == endStationId) break
            if (currentId in visited) continue
            visited.add(currentId)

            adjacencyList[currentId]?.forEach { (neighborId, weight) ->
                val newDist = currentDist + weight
                if (newDist < distances.getValue(neighborId)) {
                    distances[neighborId] = newDist
                    previous[neighborId] = currentId
                    priorityQueue.add(neighborId to newDist)
                }
            }
        }

        val path = mutableListOf<Station>()
        var current: Int? = endStationId
        if (distances[endStationId] == null || distances[endStationId] == Int.MAX_VALUE) return emptyList()

        while (current != null) {
            allStations[current]?.let { path.add(0, it) }
            current = previous[current]
        }
        return path
    }

    /**
     * Pre-populates the database with sample Namma Metro data.
     */
    suspend fun initData() {
        val currentStations = metroDao.getAllStations()
        if (currentStations.isNotEmpty()) return

        val stations = listOf(
            // Green Line
            Station(1, "Nagasandra", "ನಾಗಸಂದ್ರ", "Green", platformInfo = "Platform 1"),
            Station(2, "Yeshwanthpur", "ಯಶವಂತಪುರ", "Green", exitInfo = "Exit A for Railway Station", platformInfo = "Platform 1"),
            Station(3, "Majestic", "ಮೆಜೆಸ್ಟಿಕ್", "Interchange", isInterchange = true, exitInfo = "Exit A for KSRTC Bus Stand", platformInfo = "Platform 1 Green / Platform 2 Purple"),
            Station(4, "Silk Institute", "ಸಿಲ್ಕ್ ಇನ್‌ಸ್ಟಿಟ್ಯೂಟ್", "Green", platformInfo = "Platform 1"),
            Station(9, "Banashankari", "ಬನಶಂಕರಿ", "Green", exitInfo = "Exit B for TTMC", platformInfo = "Platform 1"),
            Station(10, "Jayanagar", "ಜಯನಗರ", "Green", platformInfo = "Platform 1"),
            
            // Purple Line
            Station(5, "Baiyappanahalli", "ಬೈಯಪ್ಪನಹಳ್ಳಿ", "Purple", exitInfo = "Exit A for Old Madras Road", platformInfo = "Platform 2"),
            Station(6, "Indiranagar", "ಇಂದಿರಾನಗರ", "Purple", platformInfo = "Platform 2"),
            Station(7, "MG Road", "ಎಂಜಿ ರಸ್ತೆ", "Purple", exitInfo = "Exit C for Church Street", platformInfo = "Platform 2"),
            Station(11, "Vidhana Soudha", "ವಿಧಾನ ಸೌಧ", "Purple", exitInfo = "Exit A for High Court", platformInfo = "Platform 2"),
            Station(8, "Challaghatta", "ಚಲ್ಲಘಟ್ಟ", "Purple", platformInfo = "Platform 2"),
            Station(12, "Kengeri", "ಕೆಂಗೇರಿ", "Purple", exitInfo = "Exit B for Bus Stand", platformInfo = "Platform 2")
        )

        val connections = listOf(
            Connection(fromStationId = 1, toStationId = 2, weight = 5, line = "Green"),
            Connection(fromStationId = 2, toStationId = 3, weight = 10, line = "Green"),
            Connection(fromStationId = 3, toStationId = 9, weight = 8, line = "Green"),
            Connection(fromStationId = 9, toStationId = 10, weight = 4, line = "Green"),
            Connection(fromStationId = 10, toStationId = 4, weight = 12, line = "Green"),
            
            Connection(fromStationId = 5, toStationId = 6, weight = 4, line = "Purple"),
            Connection(fromStationId = 6, toStationId = 7, weight = 3, line = "Purple"),
            Connection(fromStationId = 7, toStationId = 11, weight = 2, line = "Purple"),
            Connection(fromStationId = 11, toStationId = 3, weight = 3, line = "Purple"),
            Connection(fromStationId = 3, toStationId = 12, weight = 15, line = "Purple"),
            Connection(fromStationId = 12, toStationId = 8, weight = 5, line = "Purple")
        )

        metroDao.insertStations(stations)
        metroDao.insertConnections(connections)
    }
}
