package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stations")
data class Station(
    @PrimaryKey val id: Int,
    val name: String,
    val nameKn: String, // Kannada name
    val line: String, // "Purple" or "Green"
    val isInterchange: Boolean = false,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val isActive: Boolean = true,
    val exitInfo: String = "Exit Gate A", // Default exit info
    val platformInfo: String = "Platform 1" // Default platform info
)
