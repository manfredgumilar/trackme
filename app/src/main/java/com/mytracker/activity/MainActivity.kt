package com.mytracker.activity

import android.app.AlertDialog
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

class MainActivity : AppCompatActivity(), AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener  {
    private var db = DatabaseHelper(this)
    private var adapter: TrackAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // get all tracks from database
        val tracks = db.getAllTracks()

        //init Track Adapter
        adapter = TrackAdapter(this, tracks)
        lvTracks.adapter = adapter
        lvTracks.onItemClickListener = this
        lvTracks.onItemLongClickListener = this

        startRecord.setOnClickListener {
            val intent = Intent(this, RecordActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val intent = Intent(this, MapsActivity::class.java)
        intent.putExtra(Constants.INTENT_EXTRA_ID, id)
        startActivity(intent)
    }

    override fun onItemLongClick(
        parent: AdapterView<*>?,
        view: View?,
        position: Int,
        id: Long
    ): Boolean {

        // Alert Message for Delete
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle(getString(R.string.DeleteAlertTitle))
        builder.setMessage(getString(R.string.DeleteAlertMessage))
        //Display Yes Button
        builder.setPositiveButton(R.string.yes){dialog, which ->
            //Delete Track by ID
            db.deleteTrackById(id)
            //Refresh ListView
            adapter!!.notifyDataSetChanged()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        // Display No Button
        builder.setNegativeButton(R.string.no){dialog,which ->
            //No, do nothin'
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()

        return true
    }
}