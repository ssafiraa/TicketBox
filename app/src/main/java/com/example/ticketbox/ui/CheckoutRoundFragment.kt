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
import com.example.ticketbox.ui.LoginActivity.Companion.CLIENT_ID
import com.example.ticketbox.ui.LoginActivity.Companion.MY_PREFERENCES

class CheckoutRoundFragment : Fragment() {
    private val selectedFlight: ListView? = null
    private var sharedPreferences: SharedPreferences? = null
    private var databaseHelper: SQLiteOpenHelper? = null
    private var db: SQLiteDatabase? = null
    private var cursor: Cursor? = null
    private val bundle: Bundle? = null
    private var outboundFlightID = 0
    private var returnFlightID = 0
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
    private var flightNoReturn: TextView? = null
    private var originReturn: TextView? = null
    private var destinationReturn: TextView? = null
    private var departureDateReturn: TextView? = null
    private var arrivalDateReturn: TextView? = null
    private var departureTimeReturn: TextView? = null
    private var arrivalTimeReturn: TextView? = null
    private var flightDurationReturn: TextView? = null
    private var flightClassReturn: TextView? = null
    private var airlineReturn: TextView? = null
    private var bookFlight: Button? = null
    private var clientID = 0
    private var flightExists = false
    private var view: View? = null
    private var numTraveller = 0
    private var outboundFare = 0.0
    private var returnFare = 0.0
    private var totalCost = 0.0
    private var fareRet: TextView? = null
    private var traveller: TextView? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_checkout_round, container, false)
        val sharedPreferences = requireActivity().getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        outboundFlightID = sharedPreferences.getInt("OUTBOUND_FLIGHT_ID", 0)
        returnFlightID = sharedPreferences.getInt("RETURN_FLIGHT_ID", 0)
        numTraveller = sharedPreferences.getInt("ROUND_NUM_TRAVELLER", 0)
        clientID = clientID()
        airline = requireView().findViewById(R.id.txtCheckoutAirlineOutbound)
        flightNo = requireView().findViewById(R.id.txtCheckoutFlightNumberOutbound)
        origin = requireView().findViewById(R.id.txtCheckoutOriginOutbound)
        destination = requireView().findViewById(R.id.txtCheckoutDestinationOutbound)
        departureDate = requireView().findViewById(R.id.txtCheckoutDepartureDateOutbound)
        arrivalDate = requireView().findViewById(R.id.txtCheckoutArrivalDateOutbound)
        departureTime = requireView().findViewById(R.id.txtCheckoutDepartureTimeOutbound)
        arrivalTime = requireView().findViewById(R.id.txtCheckoutArrivalTimeOutbound)
        flightDuration = requireView().findViewById(R.id.txtCheckoutFlightDurationOutbound)
        flightClass = requireView().findViewById(R.id.txtCheckoutFlightClassOutbound)
        airlineReturn = requireView().findViewById(R.id.txtCheckoutAirlineReturn)
        flightNoReturn = requireView().findViewById(R.id.txtCheckoutFlightNumberReturn)
        originReturn = requireView().findViewById(R.id.txtCheckoutOriginReturn)
        destinationReturn = requireView().findViewById(R.id.txtCheckoutDestinationReturn)
        departureDateReturn = requireView().findViewById(R.id.txtCheckoutDepartureDateReturn)
        arrivalDateReturn = requireView().findViewById(R.id.txtCheckoutArrivalDateReturn)
        departureTimeReturn = requireView().findViewById(R.id.txtCheckoutDepartureTimeReturn)
        arrivalTimeReturn = requireView().findViewById(R.id.txtCheckoutArrivalTimeReturn)
        flightDurationReturn = requireView().findViewById(R.id.txtCheckoutFlightDurationReturn)
        flightClassReturn = requireView().findViewById(R.id.txtCheckoutFlightClassReturn)
        traveller = requireView().findViewById(R.id.txtCheckoutTravellerOutbound)
        fareRet = requireView().findViewById(R.id.txtCheckoutFareReturn)
        fare = requireView().findViewById(R.id.txtCheckoutFareOutbound)
        totalFare = requireView().findViewById(R.id.txtCheckoutTotalFareOutbound)
        bookFlight = requireView().findViewById(R.id.btnRoundCheckout)

        bookFlight!!.setOnClickListener { bookFlight(clientID) }
        // Inflate the layout for this fragment
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        displaySelectedOutboundFlightInfo(outboundFlightID)
        displaySelectedReturnFlightInfo(returnFlightID)
        fare!!.text = "$$outboundFare"
        fareRet!!.text = "$$returnFare"
        traveller!!.text = numTraveller.toString()
        totalCost = HelperUtilities.calculateTotalFare(outboundFare, returnFare, numTraveller)
        totalFare!!.text = "$$totalCost"
    }

    fun displaySelectedOutboundFlightInfo(id: Int) {
        try {
            databaseHelper = DatabaseHelper(activity)
            db = (databaseHelper as DatabaseHelper).getReadableDatabase()
            cursor = DatabaseHelper.Companion.selectFlight(db, id)
            if (cursor != null && cursor!!.count == 1) {
                cursor!!.moveToFirst()
                flightNo!!.text = cursor!!.getInt(1).toString()
                origin!!.text = cursor!!.getString(2)
                destination!!.text = cursor!!.getString(3)
                departureDate!!.text = cursor!!.getString(4)
                arrivalDate!!.text = cursor!!.getString(5)
                departureTime!!.text = cursor!!.getString(6)
                arrivalTime!!.text = cursor!!.getString(7)
                flightDuration!!.text = cursor!!.getString(8) + "h"
                outboundFare = cursor!!.getDouble(9)
                airline!!.text = cursor!!.getString(10)
                flightClass!!.text = cursor!!.getString(12)
            }
        } catch (ex: SQLiteException) {
            Toast.makeText(
                activity?.applicationContext,
                "Database error occurred 1",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun displaySelectedReturnFlightInfo(id: Int) {
        try {
            databaseHelper = DatabaseHelper(activity)
            db = (databaseHelper as DatabaseHelper).getReadableDatabase()
            cursor = DatabaseHelper.Companion.selectFlight(db, id)
            if (cursor != null && cursor!!.count == 1) {
                cursor!!.moveToFirst()
                flightNoReturn!!.text = cursor!!.getString(1)
                originReturn!!.text = cursor!!.getString(2)
                destinationReturn!!.text = cursor!!.getString(3)
                departureDateReturn!!.text = cursor!!.getString(4)
                arrivalDateReturn!!.text = cursor!!.getString(5)
                departureTimeReturn!!.text = cursor!!.getString(6)
                arrivalTimeReturn!!.text = cursor!!.getString(7)
                flightDurationReturn!!.text = cursor!!.getString(8)
                returnFare = cursor!!.getDouble(9)
                airlineReturn!!.text = cursor!!.getString(10)
                flightClassReturn!!.text = cursor!!.getString(12)
            }
        } catch (ex: SQLiteException) {
            Toast.makeText(
                activity?.applicationContext,
                "Database error occurred 2",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun bookFlight(clientID: Int) {
        try {
            databaseHelper = DatabaseHelper(activity)
            db = (databaseHelper as DatabaseHelper).getWritableDatabase()
            cursor = DatabaseHelper.Companion.selectItinerary(db, outboundFlightID, clientID)
            if (cursor != null && cursor!!.count > 0) {
                flightExists = true
            }
            cursor = DatabaseHelper.Companion.selectItinerary(db, returnFlightID, clientID)
            if (cursor != null && cursor!!.count > 0) {
                flightExists = true
            }
            if (flightExists) {
                roundFlightAlreadyBookedAlert().show()
            }
            if (flightExists == false) {
                DatabaseHelper.Companion.insertItinerary(
                    db,
                    outboundFlightID,
                    clientID,
                    numTraveller
                )
                DatabaseHelper.Companion.insertItinerary(db, returnFlightID, clientID, numTraveller)
                bookFlightDialog().show()
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

    fun roundFlightAlreadyBookedAlert(): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage("You already booked either the outbound or the return flight. Please select another flight and try again. ")
            .setCancelable(false)
            .setPositiveButton("Ok") { dialog, id ->
                intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
            }
        return builder.create()
    }

    fun clientID(): Int {
        val sharedPreferences = requireActivity().getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(CLIENT_ID, 0)
    }


}