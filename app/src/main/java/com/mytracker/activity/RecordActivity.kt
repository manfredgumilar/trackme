package com.mytracker.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.mytracker.R
import com.mytracker.database.DatabaseHelper
import com.mytracker.math.Calculate
import com.mytracker.model.Point
import com.mytracker.model.Track
import kotlinx.android.synthetic.main.activity_record.*
import kotlinx.android.synthetic.main.content_record.*
import java.util.*


class RecordActivity : AppCompatActivity() {

    private val permissionId = 42
    private var db = DatabaseHelper(this)
    private var track: Track? = null
    private var calc = Calculate()
    private var timer = Timer()
    private var isFirstPoint = true
    private var duration: Long = 0
    private var thisId: Long = 0
    private var wholeDist: Double = 0.0
    private var lastDist: Double = 0.0
    private var lastLat: Double = 0.0
    private var lastLon: Double = 0.0
    private var lastLocation: Location? = null
    private var lat: Double = 0.0
    private var lon: Double = 0.0
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)
        setSupportActionBar(toolbar)

        //init fused location provider
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getLastLocation()
        requestNewLocationData()
        //Set the schedule function
        timer.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    recPoint()
                }
            },
            0, 1000
        )


        stopRecord.setOnClickListener {
            track = db.getTrack(thisId)
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

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        ) {

            return true
        }

        return false
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                fusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    lastLocation = location
                }
            } else {
                Toast.makeText(this, getString(R.string.turn_on_location), Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 1000
        locationRequest.fastestInterval = 500

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.requestLocationUpdates(
            locationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            lastLocation = mLastLocation
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Granted. Start getting the location information
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun recPoint() {
        getLastLocation()
        lastLocation?.let {
            lat = calc.roundGpsCoordinates(it.latitude)
            lon = calc.roundGpsCoordinates(it.longitude)
        } ?: return
        if (isFirstPoint) {  //  Create db main record, record first point
            thisId = db.insertTrack(
                Track(
                    0,
                    System.currentTimeMillis(),
                    System.currentTimeMillis(),
                    0.0,
                    lat,
                    lon
                )
            )
            isFirstPoint = false
        } else {     // Calculate distances, update info, record next point
            if (lastLat != 0.0 && lastLon != 0.0) {
                lastDist = calc.distance(lat, lon, lastLat, lastLon)
                wholeDist += lastDist
            }
        }
        duration += 1
        runOnUiThread {
            tvDuration.text = getString(R.string.track_duration, calc.durationToString(duration))
            tvDistance.text = getString(R.string.track_distance, calc.distanceToString(wholeDist))
            tvLatLon.text = getString(R.string.track_coordinates, lat.toString(), lon.toString())
        }
        db.insertPoint(Point(0, thisId, System.currentTimeMillis(), lat, lon))
        lastLat = lat
        lastLon = lon
        return
    }
}