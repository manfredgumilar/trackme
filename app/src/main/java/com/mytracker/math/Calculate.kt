package com.mytracker.math

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class Calculate {

    // calculate Distance in m
    fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dlat=deg2rad(lat2 - lat1)
        val dlon=deg2rad(lon2 - lon1)

        val a= sin(dlat/2) * sin(dlat/2) + cos(deg2rad(lat1)) * cos(deg2rad(lat2)) * sin(dlon/2) * sin(dlon/2)
        val x= sqrt(a)
        val y= sqrt(1-a)

        val res= atan2(x,y)

        return 6371000 * 2 * res

    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

}