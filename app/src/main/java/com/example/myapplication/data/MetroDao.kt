package com.example.myapplication.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface MetroDao {
    @Query("SELECT * FROM stations")
    suspend fun getAllStations(): List<Station>

    @Query("SELECT * FROM connections")
    suspend fun getAllConnections(): List<Connection>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStations(stations: List<Station>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConnections(connections: List<Connection>)

    @Update
    suspend fun updateStation(station: Station)

    @Query("SELECT * FROM stations WHERE name = :name LIMIT 1")
    suspend fun getStationByName(name: String): Station?
}
