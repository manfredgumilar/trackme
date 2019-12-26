package com.mytracker.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.mytracker.R
import com.mytracker.adapter.TrackAdapter
import com.mytracker.constants.Constants
import com.mytracker.database.DatabaseHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), AdapterView.OnItemClickListener {
    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val intent = Intent(this, MapsActivity::class.java)
        intent.putExtra(Constants.INTENT_EXTRA_ID, id)
        startActivity(intent)
    }

    private var db = DatabaseHelper(this)
    private var adapter: TrackAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // get all notes from database
        val tracks = db.getAllTracks()

        //init NoteAdapter
        adapter = TrackAdapter(this, tracks)
        lvTracks.adapter = adapter
        lvTracks.onItemClickListener = this

        startRecord.setOnClickListener {
            val intent = Intent(this, RecordActivity::class.java)
            startActivity(intent)
        }
    }
}