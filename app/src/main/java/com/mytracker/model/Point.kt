package com.mytracker.model

data class Point(
    val id: Long,
    val trackId: Long,
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double
)