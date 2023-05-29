package com.example.ticketbox.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ticketbox.HelperUtils.HelperUtilities
import com.example.ticketbox.R
import com.example.ticketbox.database.DatabaseHelper

class CheckoutOnewayFragment : Fragment() {
    private val selectedFlight: ListView? = null
    private val sharedPreferences: SharedPreferences? = null
    private var databaseHelper: SQLiteOpenHelper? = null
    private var db: SQLiteDatabase? = null
    private var cursor: Cursor? = null
    private val bundle: Bundle? = null
    private var flightID = 0
    private val btnSelectFlight: Button? = null
    private val currentTab = 0
    private var intent: Intent? = null
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
    private var bookFlight: Button? = null
    private var clientID = 0
    private var flightExists = false
    private var numTraveller = 0
    private var numTrav: TextView? = null
    private var onewayFare = 0.0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_checkout_oneway, container, false)
        val sharedPreferences = requireActivity().getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        flightID = sharedPreferences.getInt("ONEWAY_FLIGHT_ID", 0)
        numTraveller = sharedPreferences.getInt("ONEWAY_NUM_TRAVELLER", 0)
        clientID = clientID()
        airline = view.findViewById<View>(R.id.txtCheckoutAirline) as TextView
        flightNo = view.findViewById<View>(R.id.txtCheckoutFlightNumber) as TextView
        origin = view.findViewById<View>(R.id.txtCheckoutOrigin) as TextView
        destination = view.findViewById<View>(R.id.txtCheckoutDestination) as TextView
        departureDate = view.findViewById<View>(R.id.txtCheckoutDepartureDate) as TextView
        arrivalDate = view.findViewById<View>(R.id.txtCheckoutArrivalDate) as TextView
        departureTime = view.findViewById<View>(R.id.txtCheckoutDepartureTime) as TextView
        arrivalTime = view.findViewById<View>(R.id.txtCheckoutArrivalTime) as TextView
        flightDuration = view.findViewById<View>(R.id.txtCheckoutFlightDuration) as TextView
        flightClass = view.findViewById<View>(R.id.txtCheckoutFlightClass) as TextView
        numTrav = view.findViewById<View>(R.id.txtCheckoutTraveller) as TextView
        fare = view.findViewById<View>(R.id.txtCheckoutFare) as TextView
        totalFare = view.findViewById<View>(R.id.txtCheckoutTotalFare) as TextView
        bookFlight = view.findViewById<View>(R.id.btnOneWayCheckout) as Button
        bookFlight!!.setOnClickListener { bookFlight(clientID) }

        // Inflate the layout for this fragment
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        displaySelectedFlightInfo(flightID)
        fare!!.text = "$$onewayFare"
        totalFare!!.text = HelperUtilities.calculateTotalFare(onewayFare, numTraveller).toString()
    }

    fun displaySelectedFlightInfo(id: Int) {
        try {
            databaseHelper = DatabaseHelper(activity)
            db = (databaseHelper as DatabaseHelper).getReadableDatabase()
            cursor = DatabaseHelper.Companion.selectFlight(db, id)
            if (cursor != null && cursor!!.count == 1) {
                cursor!!.moveToFirst()

                //Toast.makeText(getActivity().getApplicationContext(), String.valueOf(cursor.getCount()), Toast.LENGTH_SHORT).show();
                flightNo!!.text = cursor!!.getInt(1).toString()
                origin!!.text = cursor!!.getString(2)
                destination!!.text = cursor!!.getString(3)
                departureDate!!.text = cursor!!.getString(4)
                arrivalDate!!.text = cursor!!.getString(5)
                departureTime!!.text = cursor!!.getString(6)
                arrivalTime!!.text = cursor!!.getString(7)
                flightDuration!!.text = cursor!!.getString(8) + "h"
                onewayFare = cursor!!.getDouble(9)
                airline!!.text = cursor!!.getString(10)
                flightClass!!.text = cursor!!.getString(12)
                numTrav!!.text = numTraveller.toString()
            }
        } catch (ex: SQLiteException) {
            Toast.makeText(
                requireActivity().applicationContext,
                "Database error occurred",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun bookFlight(clientID: Int) {
        try {
            databaseHelper = DatabaseHelper(activity)
            db = (databaseHelper as DatabaseHelper).getWritableDatabase()
            cursor = DatabaseHelper.Companion.selectItinerary(db, flightID, clientID)
            if (cursor != null && cursor!!.count > 0) {
                cursor!!.moveToFirst()
                flightExists = true
                flightAlreadyBookedAlert().show()

                //Toast.makeText(getActivity().getApplicationContext(), "You already booked this flight.", Toast.LENGTH_SHORT).show();
            } else {
                flightExists = false
                DatabaseHelper.Companion.insertItinerary(db, flightID, clientID, numTraveller)
                bookFlightDialog().show()
                //Toast.makeText(getActivity().getApplicationContext(), "Your flight booked successfully.", Toast.LENGTH_SHORT).show();
            }
        } catch (e: SQLiteException) {
        }
    }

    fun bookFlightDialog(): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage("Your flight booked successfully. ")
            .setCancelable(false)
            .setPositiveButton("Ok") { dialog, id ->
                intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
            }
        return builder.create()
    }

    fun flightAlreadyBookedAlert(): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage("You already booked this flight. ")
            .setCancelable(false)
            .setPositiveButton("Ok") { dialog, id ->
                intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
            }
        return builder.create()
    }

    fun clientID(): Int {
        val sharedPreferences = activity?.getSharedPreferences(
            LoginActivity.MY_PREFERENCES,
            Context.MODE_PRIVATE
        )
        return sharedPreferences?.getInt(LoginActivity.CLIENT_ID, 0) ?: 0
    }


}