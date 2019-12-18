package com.mytracker.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.mytracker.model.Point
import com.mytracker.model.Track

class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null,DATABASE_VERSION) {

    companion object {
        // Database properties
        private const val DATABASE_NAME = "mytracks"
        private const val DATABASE_TABLE_NAME = "tracks"
        private const val DATABASE_TABLE_NAME2 = "points"
        private const val DATABASE_VERSION = 4

        // Database table column names
        private const val KEY_ID = "id"
        private const val KEY_TRACKID = "trackid"
        private const val KEY_TIMESTAMP = "timestamp"
        private const val KEY_TIMESTAMP1 = "timestamp1"
        private const val KEY_TIMESTAMP2 = "timestamp2"
        private const val KEY_DISTANCE = "distance"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"

        // Database cursor array
        private val CURSOR_ARRAY = arrayOf(
            KEY_ID,
            KEY_TIMESTAMP1,
            KEY_TIMESTAMP2,
            KEY_DISTANCE,
            KEY_LATITUDE,
            KEY_LONGITUDE
        )

        //Database create table statement
        private const val CREATE_TABLE = ("""CREATE TABLE $DATABASE_TABLE_NAME(
                $KEY_ID INTEGER PRIMARY KEY, 
                $KEY_TIMESTAMP1 INT, 
                $KEY_TIMESTAMP2 INT, 
                $KEY_DISTANCE FLOAT,
                $KEY_LATITUDE FLOAT,
                $KEY_LONGITUDE FLOAT
                )""")
        //Database create table statement
        private const val CREATE_TABLE2 = ("""CREATE TABLE $DATABASE_TABLE_NAME2(
                $KEY_ID INTEGER PRIMARY KEY, 
                $KEY_TRACKID INTEGER,
                $KEY_TIMESTAMP INT, 
                $KEY_LATITUDE FLOAT,
                $KEY_LONGITUDE FLOAT,
                FOREIGN KEY($KEY_TRACKID) REFERENCES $DATABASE_TABLE_NAME($KEY_ID)
                )""")
        //Database drop table statement
        private const val DROP_TABLE = "DROP TABLE IF EXISTS $DATABASE_TABLE_NAME"
        private const val DROP_TABLE2 = "DROP TABLE IF EXISTS $DATABASE_TABLE_NAME2"

        //Database select all statement
        private const val SELECT_ALL = "SELECT * FROM $DATABASE_TABLE_NAME"
    }

    //insert Track into Database
    fun insertPoint(point: Point): Long {
        return writableDatabase.insert(DATABASE_TABLE_NAME2, null, pointToContentValues(point))
    }

    //Get all points
    fun getAllPoints(id: Long): ArrayList<Point> {
        val points = ArrayList<Point>()
        val selId = id.toString()
        val SELECT_ALL_POINTS = "SELECT * FROM $DATABASE_TABLE_NAME2 WHERE $KEY_TRACKID = $selId"
        val cursor = readableDatabase.rawQuery(SELECT_ALL_POINTS, null)
        cursor.moveToFirst().run {
            do {
                cursorToPoint(cursor)?.let { points.add(it) }
            } while (cursor.moveToNext())
        }
        readableDatabase.close()
        return points
    }

    // create new track object from cursor
    private fun cursorToPoint(cursor: Cursor): Point? {
        if (cursor?.count ==0) return null
        var point: Point? = null
        cursor?.run {
            point = Point(
                getLong(getColumnIndex(KEY_ID)),
                getLong(getColumnIndex(KEY_TRACKID)),
                getLong(getColumnIndex(KEY_TIMESTAMP)),
                getDouble(getColumnIndex(KEY_LATITUDE)),
                getDouble(getColumnIndex(KEY_LONGITUDE))
            )
        }
        return point
    }

    // Create new ContentValues object from Track
    private fun pointToContentValues(point: Point): ContentValues {
        val values = ContentValues()
        values.put(KEY_TRACKID, point.trackid)
        values.put(KEY_TIMESTAMP, point.timestamp)
        values.put(KEY_LATITUDE, point.latitude)
        values.put(KEY_LONGITUDE, point.longitude)
        return values
    }

    //insert Track into Database
    fun insertTrack(track: Track): Long {
        return writableDatabase.insert(DATABASE_TABLE_NAME,null, trackToContentValues(track))
    }
    //Get all tracks
    fun getAllTracks(): List<Track> {
        val notes = ArrayList<Track>()
        val cursor = readableDatabase.rawQuery(SELECT_ALL, null)
        cursor.moveToFirst().run {
            do {
                cursorToTrack(cursor)?.let { notes.add(it) }
            } while (cursor.moveToNext())
        }
        readableDatabase.close()
        return notes
    }

    // get single track
    fun getTrack(id: Long): Track? {
        val track: Track?
        val cursor = readableDatabase.query(
            DATABASE_TABLE_NAME, CURSOR_ARRAY,"$KEY_ID=?",
            arrayOf(id.toString()),null,null,null,null
        )
        cursor.moveToFirst()
        track = cursorToTrack(cursor)
        cursor.close()

        return track
    }

    //update single track
    fun updateTrack(track: Track): Int {
        val db = writableDatabase

        return db.update(
            DATABASE_TABLE_NAME, trackToContentValues(track), "$KEY_ID=?",
            arrayOf(track.id.toString())
        )
    }

    //delete single track
    fun deleteTrack(track: Track) {
        val db = writableDatabase

        db.delete(
            DATABASE_TABLE_NAME, "$KEY_ID=?", arrayOf(track.id.toString())
        )
        db.delete(
            DATABASE_TABLE_NAME2, "$KEY_ID=?", arrayOf(track.id.toString())
        )
    }

    // create new track object from cursor
    private fun cursorToTrack(cursor: Cursor): Track? {
        if (cursor?.count ==0) return null
        var track: Track? = null
        cursor?.run {
            track = Track(
                getLong(getColumnIndex(KEY_ID)),
                getLong(getColumnIndex(KEY_TIMESTAMP1)),
                getLong(getColumnIndex(KEY_TIMESTAMP2)),
                getDouble(getColumnIndex(KEY_DISTANCE)),
                getDouble(getColumnIndex(KEY_LATITUDE)),
                getDouble(getColumnIndex(KEY_LONGITUDE))
            )
        }
        return track
    }

    // Create new ContentValues object from Track
    private fun trackToContentValues(track: Track): ContentValues {
        val values = ContentValues()

        values.put(KEY_TIMESTAMP1, track.timestamp1)
        values.put(KEY_TIMESTAMP2, track.timestamp2)
        values.put(KEY_DISTANCE, track.distance)
        values.put(KEY_LATITUDE, track.latitude)
        values.put(KEY_LONGITUDE, track.longitude)

        return values
    }
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE)
        db?.execSQL(CREATE_TABLE2)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DROP_TABLE)
        db?.execSQL(CREATE_TABLE)
        db?.execSQL(DROP_TABLE2)
        db?.execSQL(CREATE_TABLE2)
    }

}