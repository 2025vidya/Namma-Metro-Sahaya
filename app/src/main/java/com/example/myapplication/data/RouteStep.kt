package com.example.myapplication.data

data class RouteStep(
    val title: String,
    val titleKn: String? = null,
    val instruction: String,
    val iconRes: Int,
    val color: Int? = null,
    val isInterchange: Boolean = false
)
