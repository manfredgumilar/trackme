package com.mytracker.activity

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.mytracker.R
import com.mytracker.constants.Constants
import com.mytracker.database.DatabaseHelper
import com.mytracker.model.Track


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private var db = DatabaseHelper(this)
    private var track: Track? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        val id = intent.getLongExtra(Constants.INTENT_EXTRA_ID, Constants.INVALID_VALUE)
        if (id != Constants.INVALID_VALUE) {
            track = db.getTrack(id)
        }
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        // declare bounds object to fit whole route in screen
        val latLong = LatLngBounds.Builder()
        var start = LatLng(47.50311, 9.7471)
        // Declare polyline object and set up color and width
        val options = PolylineOptions()
        options.color(Color.RED)
        options.width(5f)
        track?.let {
            start = LatLng(it.latitude, it.longitude)
            options.add(start)
            latLong.include(start)
            val points = db.getAllPoints(it.id)
            for (point in points) {
                val latLonPoint = LatLng(point.latitude, point.longitude)
                options.add(latLonPoint)
                latLong.include(latLonPoint)
            }
        }

        val bounds = latLong.build()
        // add polyline to the map
        map.addPolyline(options)
        // Add a marker at startpoint and move the camera
        map.addMarker(MarkerOptions().position(start).title(getString(R.string.startpoint)))

        map.setOnMapLoadedCallback(GoogleMap.OnMapLoadedCallback {
            map.moveCamera(
                CameraUpdateFactory.newLatLngBounds(bounds, 100)
            )
        })

    }
}