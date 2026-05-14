package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "connections")
data class Connection(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fromStationId: Int,
    val toStationId: Int,
    val weight: Int, // Travel time or distance
    val line: String // "Purple" or "Green"
)
