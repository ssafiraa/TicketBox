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

class OutboundFlightListActivity : AppCompatActivity() {
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
    private var flightList: ListView? = null
    private val bundle: Bundle? = null
    private var currentTab = 0
    private val oneWayFightID = 0
    private var outboundFlightID = 0
    private val returnFlightID = 0
    private var intent: Intent? = null
    private var actionBar: ActionBar? = null
    private var sortByID = 0
    private var btnSort: Button? = null
    private var flightNotFound: TextView? = null
    private var flightUnavailable = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_outbound_flight_list)

        //bundle = getIntent().getExtras();
        actionBar = supportActionBar
        val sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE)
        currentTab = sharedPreferences.getInt("CURRENT_TAB", 0)
        origin = sharedPreferences.getString("ORIGIN", "")
        destination = sharedPreferences.getString("DESTINATION", "")
        departureDate = sharedPreferences.getString("DEPARTURE_DATE", "")
        returnDate = sharedPreferences.getString("RETURN_DATE", "")
        flightClass = sharedPreferences.getString("FLIGHT_CLASS", "")
        sortByID = sharedPreferences.getInt("OUTBOUND_SORT_ID", 100)
        flightList = findViewById<View>(R.id.outboundFlightList) as ListView
        btnSort = findViewById<View>(R.id.btnOutboundSort) as Button
        flightNotFound = findViewById<View>(R.id.txtOutboundFlightNotFound) as TextView
        flightNotFound!!.visibility = View.INVISIBLE
        btnSort!!.setOnClickListener { sortDialog().show() }
        searchOutboundFlight()
    }

    fun searchOutboundFlight() {
        try {
            databaseHelper = DatabaseHelper(applicationContext)
            db = databaseHelper!!.getReadableDatabase()

            //outbound flight
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
                actionBar!!.setTitle("Select outbound flight")
                actionBar!!.setSubtitle(
                    HelperUtilities.capitalize(origin) + " -> " + HelperUtilities.capitalize(
                        destination
                    )
                )
                val listAdapter: CursorAdapter = SimpleCursorAdapter(
                    applicationContext,
                    R.layout.custom_list_view,
                    cursor,
                    arrayOf(
                        "DEPARTURETIME",
                        "ARRIVALTIME",
                        "FARE",
                        "AIRLINENAME",
                        "FLIGHTDURATION",
                        "FLIGHTCLASSNAME"
                    ),
                    intArrayOf(
                        R.id.txtDepartureTime,
                        R.id.txtArrivalTime,
                        R.id.txtFare,
                        R.id.txtAirline,
                        R.id.txtTravelTime,
                        R.id.txtClass
                    ),  //map the contents of NAME col to text in ListView
                    0
                )
                flightList!!.adapter = listAdapter
            } else {
                flightUnavailable = true
            }
            cursor = DatabaseHelper.Companion.selectFlight(
                db, destination, origin,
                returnDate, flightClass
            )
            if (cursor != null && cursor!!.count > 0) {
                //do nothing here
            } else {
                flightUnavailable = true
            }

            //Toast.makeText(getApplicationContext(), String.valueOf(flightUnavailable), Toast.LENGTH_SHORT).show();
            if (flightUnavailable == true) {
                flightNotFound!!.visibility = View.VISIBLE
                btnSort!!.visibility = View.INVISIBLE
            }
            flightList!!.onItemClickListener =
                OnItemClickListener { adapterView, view, position, id ->
                    outboundFlightID = id.toInt()
                    intent = Intent(applicationContext, ReturnFlightListActivity::class.java)
                    val sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.remove("OUTBOUND_FLIGHT_ID")
                    editor.putInt("OUTBOUND_FLIGHT_ID", outboundFlightID)
                    editor.commit()
                    startActivity(intent)
                    finish()
                }
        } catch (e: SQLiteException) {
            Toast.makeText(applicationContext, "Database error", Toast.LENGTH_SHORT).show()
        }
    }

    fun flightNotFoundDialog(): Dialog {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("The specified round flight is not available. Please try again later.")
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, id ->
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        return builder.create()
    }

    //sort by dialog (outbound)
    fun sortDialog(): Dialog {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Sort by")
            .setItems(R.array.sort) { dialogInterface, id ->
                val sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.remove("OUTBOUND_SORT_ID")
                editor.putInt("OUTBOUND_SORT_ID", id)
                editor.commit()
                intent = Intent(applicationContext, OutboundFlightListActivity::class.java)
                startActivity(intent)
            }
        return builder.create()
    }
}