package com.mytracker.math

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class Calculate {


    fun durationAndDistanceToString(duration: Long, distance: Double): String {
        return "Dauer: ${durationToString(duration)}, Distanz: ${distanceToString(distance)}"
    }

    fun durationToString(duration: Long): String {
        return if (duration > 60) {
            val minutes = duration / 60
            val seconds = duration % 60
            "$minutes Minute(n) $seconds Sekunde(n)"
        } else {
            "$duration Sekunde(n)"
        }
    }

    fun distanceToString(distance: Double): String {
        return if (distance > 1000) {
            val kilometers = (distance / 1000).toInt()
            val meters = (distance % 1000).toInt()
            "$kilometers Kilometer $meters Meter"

        } else {
            "${distance.toInt()} Meter"
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