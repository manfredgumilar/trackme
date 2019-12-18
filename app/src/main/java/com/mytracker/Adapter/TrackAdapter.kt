package com.mytracker.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.mytracker.R
import com.mytracker.model.Track
import java.lang.Math.round
import java.util.*
import kotlin.math.roundToInt


class TrackAdapter(context: Context, var tracks: List<Track>): BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = inflater.inflate(R.layout.list_item_view, parent, false)

            holder = ViewHolder()
            holder.title = view.findViewById(R.id.tvDateTime) as TextView
            holder.text = view.findViewById(R.id.tvDistDuration) as TextView

            // Hang onto this holder for future recycling by using setTag
            view.tag = holder
        } else {
            // skip all the expensive inflation steps and just get the holder you already made
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        // Get relevant subviews of the row view
        val tvTrackTitle = holder.title
        val tvTrackText = holder.text

        // Get our note object for current position using getItem(position).
        val track = getItem(position) as Track

        tvTrackTitle.text = Date(track.timestamp1).toString()
        val duration=(track.timestamp2-track.timestamp1) / 1000
        tvTrackText.text = track.distance.toString() + "m   " + duration.toString() + "sec"

        // return view containing all text values for current position
        return view
    }

    override fun getItem(position: Int): Any {
        return tracks[position]
    }

    override fun getItemId(position: Int): Long {
        return tracks[position].id
    }

    override fun getCount(): Int {
        return tracks.size
    }

    private class ViewHolder {
        lateinit var title: TextView
        lateinit var text: TextView
    }

}