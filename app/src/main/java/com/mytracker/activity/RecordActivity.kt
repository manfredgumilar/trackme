package com.mytracker.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mytracker.R
import com.mytracker.constants.Constants
import com.mytracker.math.Calculate
import com.mytracker.constants.Constants.Companion.REQUEST_CORE_LOCATION

import kotlinx.android.synthetic.main.activity_record.*
import kotlinx.android.synthetic.main.activity_record.toolbar
import kotlinx.android.synthetic.main.content_record.*
import java.util.*
import com.mytracker.database.DatabaseHelper
import com.mytracker.model.Point
import com.mytracker.model.Track
import kotlin.math.round


class RecordActivity : AppCompatActivity() {

    private var db = DatabaseHelper(this)
    private var track: Track? = null
    private var calc = Calculate()
    private var timer = Timer()
    private var isFirstPoint = true
    private var duration: Int = 0
    private var thisId: Long = 0
    private var wholeDist: Double= 0.0
    private var lastDist: Double= 0.0
    private var lastLat: Double = 0.0
    private var lastLon: Double = 0.0
    private var lastLocation: Location? =null
    private var lat: Double = 0.0
    private var lon: Double = 0.0
    private lateinit var fusedLocationClient: FusedLocationProviderClient



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)
        setSupportActionBar(toolbar)

        //init fused location provider
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        //Set the schedule function


        checkLastLocation()
        timer.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    recPoint()
                }
            },
            0, 5000
        )   // 1000 Millisecond  = 1 second


        stopRecord.setOnClickListener {
            track=db.getTrack(thisId)
            track?.let {
                it.timestamp2 = System.currentTimeMillis()
                it.distance = wholeDist
                db.updateTrack(it)
            }

            val intent = Intent(this, MainActivity::class.java)
            timer.cancel()
            startActivity(intent)
        }
    }
    private fun checkPermission(): Boolean {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED ) {

            return true
        }

        return false
    }

    private fun checkLastLocation() {



        if(checkPermission()) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    lastLocation = location
//                    latitude =  location?.latitude
//                    longitude = location?.longitude
                }
//            fusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
//                lastLocation = task.result
//            }
        }else {
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_CORE_LOCATION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permission: Array<String>, grantResults: IntArray) {
        if(requestCode == Constants.REQUEST_CORE_LOCATION) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLastLocation()
            }
        }
    }


    private fun recPoint() {
        checkLastLocation()
        lastLocation?.let {
            lat = it.latitude
            lon = it.longitude
        }  ?:  return
        if(isFirstPoint) {  //  Create db main record, record first point
            thisId = db.insertTrack(Track(0, System.currentTimeMillis(), System.currentTimeMillis(), 0.0, lat, lon))
            isFirstPoint=false
        } else {     // Calculate distances, update info, record next point
            if(lastLat!=0.0 && lastLon!= 0.0) {
                lastDist = calc.distance(lat, lon, lastLat, lastLon)
                wholeDist += lastDist
            }
        }
        duration += 5
        runOnUiThread {
            tvDuration.setText("Dauer: " + duration.toString())
            tvDistance.setText("Distanz: " + round(wholeDist).toString())
            tvLatLon.setText(lat.toString() + " / " + lon.toString())
        }
        var pointId = db.insertPoint(Point(0, thisId, System.currentTimeMillis(), lat, lon))
        lastLat = lat
        lastLon = lon
        return
    }

}
