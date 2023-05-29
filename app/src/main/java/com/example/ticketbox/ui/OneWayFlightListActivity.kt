package com.example.ticketbox.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.Button
import android.widget.CursorAdapter
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.ticketbox.HelperUtils.HelperUtilities
import com.example.ticketbox.R
import com.example.ticketbox.database.DatabaseHelper

class OneWayFlightListActivity : AppCompatActivity() {
    private var sharedPreferences: SharedPreferences? = null
    private var databaseHelper: SQLiteOpenHelper? = null
    private var db: SQLiteDatabase? = null
    private var cursor: Cursor? = null
    private var origin: String? = null
    private var destination: String? = null
    private var departureDate: String? = null
    private var returnDate: String? = null
    private var flightClass: String? = null
    private val txtMessage: TextView? = null
    private var oneWayFlightList: ListView? = null
    private val bundle: Bundle? = null
    private var currentTab = 0
    private var oneWayFightID = 0
    private val outboundFlightID = 0
    private val returnFlightID = 0
    private var intent: Intent? = null
    private var actionBar: ActionBar? = null

    //custom dialog view
    private val dialogLayout: View? = null
    private val txtSortBy: TextView? = null
    private val sortList: ListView? = null
    private var btnSort: Button? = null
    private var sortByID = 100
    private var flightNotFound: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_way_flight_list)
        actionBar = supportActionBar
        val sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE)
        currentTab = sharedPreferences.getInt("CURRENT_TAB", 0)
        origin = sharedPreferences.getString("ORIGIN", "")
        destination = sharedPreferences.getString("DESTINATION", "")
        departureDate = sharedPreferences.getString("DEPARTURE_DATE", "")
        returnDate = sharedPreferences.getString("RETURN_DATE", "")
        flightClass = sharedPreferences.getString("FLIGHT_CLASS", "")
        sortByID = sharedPreferences.getInt("ONEWAY_SORT_ID", 100)

        //Toast.makeText(getApplicationContext(), flightClass, Toast.LENGTH_SHORT).show();
        btnSort = findViewById<View>(R.id.btnSort) as Button
        flightNotFound = findViewById<View>(R.id.txtOneWayFlightNotFound) as TextView
        flightNotFound!!.visibility = View.INVISIBLE
        btnSort!!.setOnClickListener { sortDialog().show() }
        oneWayFlightList = findViewById<View>(R.id.onewayFlightList) as ListView
        searchOneWayFlight(sortByID)
    }

    fun searchOneWayFlight(sortByID: Int) {
        try {
            databaseHelper = DatabaseHelper(applicationContext)
            db = databaseHelper!!.getReadableDatabase()
            cursor = if (sortByID == 0) {
                DatabaseHelper.Companion.selectFlight(
                    db, origin, destination,
                    departureDate, flightClass, "FARE"
                )
            } else if (sortByID == 1) {
                DatabaseHelper.Companion.selectFlight(
                    db, origin, destination,
                    departureDate, flightClass, "FLIGHTDURATION"
                )
            } else {
                DatabaseHelper.Companion.selectFlight(
                    db, origin, destination,
                    departureDate, flightClass
                )
            }
            if (cursor != null && cursor!!.count > 0) {
                actionBar!!.setTitle("Select one way flight")
                actionBar!!.setSubtitle(
                    HelperUtilities.capitalize(origin) + " -> " + HelperUtilities.capitalize(
                        destination
                    )
                )
                val listAdapter: CursorAdapter = SimpleCursorAdapter(
                    applicationContext,
                    R.layout.custom_list_view,
                    cursor, arrayOf(
                        "DEPARTURETIME", "ARRIVALTIME", "FARE", "AIRLINENAME",
                        "FLIGHTDURATION", "FLIGHTCLASSNAME"
                    ), intArrayOf(
                        R.id.txtDepartureTime, R.id.txtArrivalTime, R.id.txtFare,
                        R.id.txtAirline, R.id.txtTravelTime, R.id.txtClass
                    ),
                    0
                )
                oneWayFlightList!!.adapter = listAdapter
            } else {
                flightNotFound!!.visibility = View.VISIBLE
                btnSort!!.visibility = View.INVISIBLE
            }
            oneWayFlightList!!.onItemClickListener =
                OnItemClickListener { adapterView, view, position, id ->
                    oneWayFightID = id.toInt()
                    intent = Intent(applicationContext, CheckOutActivity::class.java)
                    val sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.remove("ONEWAY_FLIGHT_ID")
                    editor.putInt("ONEWAY_FLIGHT_ID", oneWayFightID)
                    editor.commit()
                    startActivity(intent)
                    finish()
                }
        } catch (e: SQLiteException) {
            Toast.makeText(applicationContext, "Database error", Toast.LENGTH_SHORT).show()
        }
    }

    //sort by dialog (one way)
    fun sortDialog(): Dialog {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Sort by")
            .setItems(R.array.sort) { dialogInterface, id ->
                val sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.remove("ONEWAY_SORT_ID")
                editor.putInt("ONEWAY_SORT_ID", id)
                editor.commit()
                intent = Intent(applicationContext, OneWayFlightListActivity::class.java)
                startActivity(intent)
            }
        return builder.create()
    }
}