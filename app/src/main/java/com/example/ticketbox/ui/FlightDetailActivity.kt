package com.example.ticketbox.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ticketbox.HelperUtils.HelperUtilities
import com.example.ticketbox.R
import com.example.ticketbox.database.DatabaseHelper

class FlightDetailActivity : AppCompatActivity() {
    private var databaseHelper: SQLiteOpenHelper? = null
    private var db: SQLiteDatabase? = null
    private var cursor: Cursor? = null
    private var flightID = 0
    private var flightNo: TextView? = null
    private var origin: TextView? = null
    private var destination: TextView? = null
    private var departureDate: TextView? = null
    private var arrivalDate: TextView? = null
    private var departureTime: TextView? = null
    private var arrivalTime: TextView? = null
    private var flightDuration: TextView? = null
    private var flightClass: TextView? = null
    private var airline: TextView? = null
    private var fare: TextView? = null
    private var totalFare: TextView? = null
    private var btnCancelFlight: Button? = null
    private var intent: Intent? = null
    private var itineraryID = 0
    private var traveller: TextView? = null
    private var numTraveller = 0
    private var singleFare = 0.0
    private var totalCost = 0.0
    private var fName: TextView? = null
    private var cCard: TextView? = null
    private var timeStamp: TextView? = null
    private var clientID = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flight_detail)
        val intent = getIntent()
        flightID = intent.getIntExtra("FLIGHT_ID", 0)
        clientID = clientID()
        fName = findViewById<View>(R.id.txtFName) as TextView
        cCard = findViewById<View>(R.id.txtCCard) as TextView
        timeStamp = findViewById<View>(R.id.txtTimeStamp) as TextView
        airline = findViewById<View>(R.id.txtAirlineDetail) as TextView
        flightNo = findViewById<View>(R.id.txtFlightNumberDetail) as TextView
        origin = findViewById<View>(R.id.txtOriginDetail) as TextView
        destination = findViewById<View>(R.id.txtDestinationDetail) as TextView
        departureDate = findViewById<View>(R.id.txtDepartureDateDetail) as TextView
        arrivalDate = findViewById<View>(R.id.txtArrivalDateDetail) as TextView
        departureTime = findViewById<View>(R.id.txtDepartureTimeDetail) as TextView
        arrivalTime = findViewById<View>(R.id.txtArrivalTimeDetail) as TextView
        flightDuration = findViewById<View>(R.id.txtFlightDurationDetail) as TextView
        flightClass = findViewById<View>(R.id.txtFlightClassDetail) as TextView
        traveller = findViewById<View>(R.id.txtTravellerDetail) as TextView
        fare = findViewById<View>(R.id.txtFareDetail) as TextView
        totalFare = findViewById<View>(R.id.txtTotalFareDetail) as TextView
        displaySelectedFlightInfo(flightID)
        totalCost = HelperUtilities.calculateTotalFare(singleFare, numTraveller)
        traveller!!.text = numTraveller.toString()
        fare!!.text = "$$singleFare"
        totalFare!!.text = "$$totalCost"
        btnCancelFlight = findViewById<View>(R.id.btnCancelFlight) as Button
        btnCancelFlight!!.setOnClickListener { cancelFlightAlert().show() }
    }

    fun displaySelectedFlightInfo(id: Int) {
        try {
            databaseHelper = DatabaseHelper(applicationContext)
            db = databaseHelper!!.getReadableDatabase()
            cursor = DatabaseHelper.Companion.getItineraryDetail(db, id)
            if (cursor != null && cursor!!.count > 0) {
                cursor!!.moveToFirst()
                flightNo!!.text = cursor!!.getInt(1).toString()
                origin!!.text = cursor!!.getString(2)
                destination!!.text = cursor!!.getString(3)
                departureDate!!.text = cursor!!.getString(4)
                arrivalDate!!.text = cursor!!.getString(5)
                departureTime!!.text = cursor!!.getString(6)
                arrivalTime!!.text = cursor!!.getString(7)
                flightDuration!!.text = cursor!!.getString(8) + "h"
                singleFare = cursor!!.getDouble(9)
                airline!!.text = cursor!!.getString(10)
                flightClass!!.text = cursor!!.getString(12)
                numTraveller = cursor!!.getInt(13)
                timeStamp!!.text = cursor!!.getString(14)
            }
            cursor = DatabaseHelper.Companion.selectClientJoinAccount(db, clientID)
            if (cursor != null && cursor!!.count == 1) {
                cursor!!.moveToFirst()
                fName!!.text =
                    HelperUtilities.capitalize(cursor!!.getString(0)) + " " + HelperUtilities.capitalize(
                        cursor!!.getString(1)
                    )

            }
        } catch (ex: SQLiteException) {
        }
    }

    fun cancelFlight() {
        try {
            databaseHelper = DatabaseHelper(applicationContext)
            db = databaseHelper!!.getWritableDatabase()
            clientID = clientID()
            cursor = DatabaseHelper.Companion.selectItinerary(db, flightID, clientID)
            if (cursor != null && cursor!!.count > 0) {
                cursor!!.moveToFirst()
                itineraryID = cursor!!.getInt(0)

                // Toast.makeText(getApplicationContext(), String.valueOf(itineraryID), Toast.LENGTH_SHORT).show();
            }
            DatabaseHelper.Companion.deleteItinerary(db, itineraryID)
        } catch (e: SQLiteException) {
        }
    }

    fun cancelFlightAlert(): Dialog {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to cancel your flight? ")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                cancelFlight()
                val intent = Intent(applicationContext, ItineraryActivity::class.java)
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialogInterface, i -> }
        return builder.create()
    }

    fun clientID(): Int {
        val sharedPreferences = getSharedPreferences(LoginActivity.MY_PREFERENCES, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(LoginActivity.CLIENT_ID, 0)
    }
}