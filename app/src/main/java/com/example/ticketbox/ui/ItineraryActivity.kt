package com.example.ticketbox.ui

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.CursorAdapter
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ticketbox.R
import com.example.ticketbox.database.DatabaseHelper

class ItineraryActivity : AppCompatActivity() {
    private var databaseHelper: SQLiteOpenHelper? = null
    private var db: SQLiteDatabase? = null
    private var cursor: Cursor? = null
    private var flightID = 0
    private var clientID = 0
    private var intent: Intent? = null
    private var flightList: ListView? = null
    private var itineraryMessage: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_itinerary)
        flightList = findViewById<View>(R.id.itinerary) as ListView
        itineraryMessage = findViewById<View>(R.id.itineraryMessage) as TextView
        clientID = clientID()
        itineraryMessage!!.visibility = View.INVISIBLE
        try {
            databaseHelper = DatabaseHelper(applicationContext)
            db = databaseHelper!!.getReadableDatabase()
            cursor = DatabaseHelper.Companion.selectItinerary(db, clientID)
            if (cursor != null && cursor!!.count > 0) {

                //Toast.makeText(getApplicationContext(), String.valueOf(cursor.getCount()), Toast.LENGTH_SHORT).show();
                val listAdapter: CursorAdapter = SimpleCursorAdapter(
                    applicationContext,
                    R.layout.custom_itinerary_view,
                    cursor, arrayOf(
                        "ORIGIN", "DESTINATION", "AIRLINENAME",
                        "FLIGHTDURATION", "FLIGHTCLASSNAME", "DEPARTUREDATE"
                    ), intArrayOf(
                        R.id.txtOriginList,
                        R.id.txtDestinationList,
                        R.id.txtAirline,
                        R.id.txtTravelTime,
                        R.id.txtClass,
                        R.id.txtDepartureDateList
                    ),
                    0
                )
                flightList!!.adapter = listAdapter
            } else {
                itineraryMessage!!.visibility = View.VISIBLE
            }
            flightList!!.onItemClickListener =
                OnItemClickListener { adapterView, view, position, id ->
                    flightID = id.toInt()
                    //Toast.makeText(getApplicationContext(), String.valueOf(flightID), Toast.LENGTH_SHORT).show();
                    intent = Intent(applicationContext, FlightDetailActivity::class.java)
                    intent!!.putExtra("FLIGHT_ID", flightID)
                    startActivity(intent)
                }
        } catch (e: SQLiteException) {
            Toast.makeText(applicationContext, "Database unavailable", Toast.LENGTH_SHORT).show()
        }
    }

    fun clientID(): Int {
        val sharedPreferences = getSharedPreferences(LoginActivity.MY_PREFERENCES, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(LoginActivity.CLIENT_ID, 0)
    }
}