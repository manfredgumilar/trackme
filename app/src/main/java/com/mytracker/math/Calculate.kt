package com.mytracker.math

import kotlin.math.*

class Calculate {

    fun roundGpsCoordinates(coordinate: Double): Double {
        return (coordinate * 100000.0).roundToLong() / 100000.0
    }

    fun durationAndDistanceToString(duration: Long, distance: Double): String {
        return "Dauer: ${durationToString(duration)}, Distanz: ${distanceToString(distance)}"
    }

    fun durationToString(duration: Long): String {
        return if (duration > 60) {
            val minutes = duration / 60
            val seconds = duration % 60
            "$minutes Min. $seconds Sek."
        } else {
            "$duration Sek."
        }
    }

    fun distanceToString(distance: Double): String {
        return if (distance > 1000) {
            val kilometers = (distance / 1000).toInt()
            val meters = (distance % 1000).toInt()
            "$kilometers KM $meters M"

        } else {
            "${distance.toInt()} M"
        }
    }

    fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val lat = deg2rad(lat2 - lat1)
        val lon = deg2rad(lon2 - lon1)

        val a =
            sin(lat / 2) * sin(lat / 2) + cos(deg2rad(lat1)) * cos(deg2rad(lat2)) * sin(lon / 2) * sin(
                lon / 2
            )
        val x = sqrt(a)
        val y = sqrt(1 - a)

        val res = atan2(x, y)

        return 6371000 * 2 * res

    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }
}