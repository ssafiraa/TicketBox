package com.example.ticketbox.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TabHost
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.ticketbox.HelperUtils.HelperUtilities
import com.example.ticketbox.R
import com.example.ticketbox.database.DatabaseHelper
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var sharedPreferences: SharedPreferences? = null
    private var databaseHelper: SQLiteOpenHelper? = null
    private var db: SQLiteDatabase? = null
    private var cursor: Cursor? = null
    private var intent: Intent? = null
    private var currentTab = 0

    //date picker dialog
    private var datePickerDialog1: DatePickerDialog? = null
    private var datePickerDialog2: DatePickerDialog? = null
    private var datePickerDialog3: DatePickerDialog? = null

    //current date
    private var year = 0
    private var month = 0
    private var day = 0

    //id of date picker controls
    private val ONE_WAY_DEPARTURE_DATE_PICKER = 1
    private val ROUND_DEPARTURE_DATE_PICKER = 2
    private val ROUND_RETURN_DATE_PICKER = 3

    //traveller count
    private var oneWayTravellerCount = 1
    private var roundTravellerCount = 1

    //traveller count view
    private var numTraveller: TextView? = null

    //add and remove image button controls in the dialog
    private var imgBtnAdd: ImageButton? = null
    private var imgBtnRemove: ImageButton? = null

    //custom dialog view
    private var dialogLayout: View? = null

    //one way UI controls
    private var txtOneWayFrom: AutoCompleteTextView? = null
    private var txtOneWayTo: AutoCompleteTextView? = null
    private var btnOneWayDepartureDatePicker: Button? = null
    private var btnOneWayClass: Button? = null
    private var btnOneWayNumTraveller: Button? = null

    //round trip UI controls
    private var txtRoundFrom: AutoCompleteTextView? = null
    private var txtRoundTo: AutoCompleteTextView? = null
    private var btnRoundDepartureDatePicker: Button? = null
    private var btnRoundReturnDatePicker: Button? = null
    private var btnRoundClass: Button? = null
    private var btnRoundNumTraveller: Button? = null

    //search button
    private var btnSearch: Button? = null
    private var tempOneWaySelectedClassID = 0
    private var tempRoundSelectedClassID = 0
    private var oneWayDepartureDate: String? = null
    private var roundDepartureDate: String? = null
    private var roundReturnDate: String? = null
    private var header: View? = null
    private var imgProfile: ImageView? = null
    private var clientID = 0
    private var tempYear = 0
    private var tempMonth = 0
    private var tempDay = 0
    private val isValidOneWayDate = true
    private var isValidRoundDate = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        //navigation drawer manager
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)
        header = navigationView.getHeaderView(0)
        clientID = clientID()

        //tab host manager
        val tabHost = findViewById<View>(R.id.tabhost) as TabHost
        tabHost.setup()

        //Tab 1
        var spec = tabHost.newTabSpec("Tab One")
        spec.setContent(R.id.tab1)
        spec.setIndicator("One way")
        tabHost.addTab(spec)

        //Tab 2
        spec = tabHost.newTabSpec("Tab Two")
        spec.setContent(R.id.tab2)
        spec.setIndicator("Round Trip")
        tabHost.addTab(spec)


        //tab text color
        for (i in 0 until tabHost.tabWidget.childCount) {
            val tv =
                tabHost.tabWidget.getChildAt(i).findViewById<View>(android.R.id.title) as TextView
            tv.setTextColor(resources.getColor(R.color.white))
        }
        tabHost.setOnTabChangedListener { currentTab = tabHost.currentTab }
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_dropdown_item_1line, DatabaseHelper.Companion.CITIES
        )

        //one way form
        txtOneWayFrom = findViewById<View>(R.id.txtOneWayFrom) as AutoCompleteTextView
        txtOneWayFrom!!.setAdapter(adapter)
        txtOneWayTo = findViewById<View>(R.id.txtOneWayTo) as AutoCompleteTextView
        txtOneWayTo!!.setAdapter(adapter)
        btnOneWayDepartureDatePicker =
            findViewById<View>(R.id.btnOneWayDepartureDatePicker) as Button
        btnOneWayClass = findViewById<View>(R.id.btnOneWayClass) as Button
        btnOneWayNumTraveller = findViewById<View>(R.id.btnOneWayNumTraveller) as Button


        //round trip form
        txtRoundFrom = findViewById<View>(R.id.txtRoundFrom) as AutoCompleteTextView
        txtRoundFrom!!.setAdapter(adapter)
        txtRoundTo = findViewById<View>(R.id.txtRoundTo) as AutoCompleteTextView
        txtRoundTo!!.setAdapter(adapter)
        btnRoundDepartureDatePicker = findViewById<View>(R.id.btnRoundDepartureDatePicker) as Button
        btnRoundReturnDatePicker = findViewById<View>(R.id.btnRoundReturnDatePicker) as Button
        btnRoundClass = findViewById<View>(R.id.btnRoundClass) as Button
        btnRoundNumTraveller = findViewById<View>(R.id.btnRoundTraveller) as Button
        btnSearch = findViewById<View>(R.id.btnSearch) as Button
        imgProfile = header!!.findViewById<View>(R.id.imgProfile) as ImageView
        year = HelperUtilities.currentYear()
        month = HelperUtilities.currentMonth()
        day = HelperUtilities.currentDay()
        drawerProfileInfo()
        loadImage(clientID)


        //one way departure date picker on click listener
        btnOneWayDepartureDatePicker!!.setOnClickListener {
            datePickerDialog(
                ONE_WAY_DEPARTURE_DATE_PICKER
            )!!.show()
        }

        //round trip departure date picker on click listener
        btnRoundDepartureDatePicker!!.setOnClickListener {
            datePickerDialog(
                ROUND_DEPARTURE_DATE_PICKER
            )!!.show()
        }

        //round trip return date picker on click listener
        btnRoundReturnDatePicker!!.setOnClickListener { datePickerDialog(ROUND_RETURN_DATE_PICKER)!!.show() }


        //one way class selector on click listener
        btnOneWayClass!!.setOnClickListener { oneWayClassPickerDialog().show() }

        //one way number of travellers on click listener
        btnOneWayNumTraveller!!.setOnClickListener { oneWayNumTravellerDialog().show() }

        //round trip class selector on click listener
        btnRoundClass!!.setOnClickListener { roundClassPickerDialog().show() }

        // round trip number of traveller on click listener
        btnRoundNumTraveller!!.setOnClickListener { roundNumTravellerDialog().show() }

        //searches available flights on click
        btnSearch!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {

                //call search method here
                if (currentTab == 0) {
                    if (isValidOneWayInput && isValidOneWayDate) {
                        searchOneWayFlight()
                    }
                } else if (currentTab == 1) {
                    if (isValidRoundInput && isValidRoundDate) {
                        searchRoundFlight()
                    }
                }
            }
        })
    }

    override fun onBackPressed() {
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handles navigation view item on clicks
        val id = item.itemId
        if (id == R.id.nav_itinerary) {
            val intent = Intent(applicationContext, ItineraryActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.nav_profile) {
            val intent = Intent(applicationContext, ProfileActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.nav_security) {
            val intent = Intent(applicationContext, SecurityActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.nav_logout) {
            applicationContext.getSharedPreferences(LoginActivity.Companion.MY_PREFERENCES, 0)
                .edit().clear().commit()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    //one way class picker dialog
    fun oneWayClassPickerDialog(): Dialog {
        val builder = AlertDialog.Builder(this)
        val classList = arrayOf<CharSequence>(
            "Economy",
            "Business"
        ) //temp data, should be retrieved from database
        builder.setTitle("Select Class")
            .setSingleChoiceItems(classList, tempOneWaySelectedClassID) { dialogInterface, id ->
                tempOneWaySelectedClassID = id
                //get selected class here
                btnOneWayClass!!.text = classList[id].toString()
            }
            .setPositiveButton("OK") { dialog, id -> }
            .setNegativeButton("Cancel") { dialog, id -> }
        btnOneWayClass!!.text = classList[tempOneWaySelectedClassID].toString()
        return builder.create()
    }

    //round class picker dialog
    fun roundClassPickerDialog(): Dialog {
        val builder = AlertDialog.Builder(this)
        val classList = arrayOf<CharSequence>(
            "Economy",
            "Business"
        ) //temp data, should be retrieved from database
        builder.setTitle("Select Class")
            .setSingleChoiceItems(classList, tempRoundSelectedClassID) { dialogInterface, id ->
                tempRoundSelectedClassID = id
                //get selected class here
                btnRoundClass!!.text = classList[id].toString()
            }
            .setPositiveButton("OK") { dialog, id -> }
            .setNegativeButton("Cancel") { dialog, id -> }
        btnRoundClass!!.text = classList[tempRoundSelectedClassID].toString()
        return builder.create()
    }

    //number of travellers dialog (one way)
    fun oneWayNumTravellerDialog(): Dialog {
        dialogLayout = layoutInflater.inflate(R.layout.custom_dialog, null)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Number of travellers")
            .setView(dialogLayout)
            .setPositiveButton("OK") { dialog, id ->
                //get number of traveller here
            }
            .setNegativeButton("Cancel") { dialog, id -> }
        imgBtnRemove = dialogLayout!!.findViewById<View>(R.id.imgBtnRemove) as ImageButton
        imgBtnAdd = dialogLayout!!.findViewById<View>(R.id.imgBtnAdd) as ImageButton
        numTraveller = dialogLayout!!.findViewById<View>(R.id.txtNumber) as TextView
        imgBtnAdd!!.setOnClickListener {
            oneWayTravellerCount++
            numTraveller!!.text = oneWayTravellerCount.toString()
            btnOneWayNumTraveller!!.text = "$oneWayTravellerCount Traveller"
        }
        imgBtnRemove!!.setOnClickListener {
            if (oneWayTravellerCount > 1) {
                oneWayTravellerCount--
            }
            numTraveller!!.text = oneWayTravellerCount.toString()
            btnOneWayNumTraveller!!.text = "$oneWayTravellerCount Traveller"
        }
        numTraveller!!.text = oneWayTravellerCount.toString()
        return builder.create()
    }

    //number of travellers dialog (round trip)
    fun roundNumTravellerDialog(): Dialog {
        dialogLayout = layoutInflater.inflate(R.layout.custom_dialog, null)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Number of travellers")
            .setView(dialogLayout)
            .setPositiveButton("OK") { dialog, id ->
                //get number of traveller here
            }
            .setNegativeButton("Cancel") { dialog, id -> }
        imgBtnRemove = dialogLayout!!.findViewById<View>(R.id.imgBtnRemove) as ImageButton
        imgBtnAdd = dialogLayout!!.findViewById<View>(R.id.imgBtnAdd) as ImageButton
        numTraveller = dialogLayout!!.findViewById<View>(R.id.txtNumber) as TextView
        imgBtnAdd!!.setOnClickListener {
            roundTravellerCount++
            numTraveller!!.text = roundTravellerCount.toString()
            btnRoundNumTraveller!!.text = "$roundTravellerCount Traveller"
        }
        imgBtnRemove!!.setOnClickListener {
            if (roundTravellerCount > 1) {
                roundTravellerCount--
            }
            numTraveller!!.text = roundTravellerCount.toString()
            btnRoundNumTraveller!!.text = "$roundTravellerCount Traveller"
        }
        numTraveller!!.text = roundTravellerCount.toString()
        return builder.create()
    }

    fun datePickerDialog(datePickerId: Int): DatePickerDialog? {
        when (datePickerId) {
            ONE_WAY_DEPARTURE_DATE_PICKER -> {
                if (datePickerDialog1 == null) {
                    datePickerDialog1 =
                        DatePickerDialog(this, oneWayDepartureDatePickerListener, year, month, day)
                }
                datePickerDialog1!!.datePicker.minDate = System.currentTimeMillis() - 1000
                return datePickerDialog1
            }

            ROUND_DEPARTURE_DATE_PICKER -> {
                if (datePickerDialog2 == null) {
                    datePickerDialog2 =
                        DatePickerDialog(this, roundDepartureDatePickerListener, year, month, day)
                }
                datePickerDialog2!!.datePicker.minDate = System.currentTimeMillis() - 1000
                return datePickerDialog2
            }

            ROUND_RETURN_DATE_PICKER -> {
                if (datePickerDialog3 == null) {
                    datePickerDialog3 =
                        DatePickerDialog(this, roundReturnDatePickerListener, year, month, day)
                }
                datePickerDialog3!!.datePicker.minDate = System.currentTimeMillis() - 1000
                return datePickerDialog3
            }
        }
        return null
    }

    //get one way departure date here
    val oneWayDepartureDatePickerListener: OnDateSetListener
        get() = OnDateSetListener { datePicker, startYear, startMonth, startDay -> //get one way departure date here
            oneWayDepartureDate = startYear.toString() + "-" + (startMonth + 1) + "-" + startDay
            btnOneWayDepartureDatePicker!!.text =
                HelperUtilities.formatDate(startYear, startMonth, startDay)
        }

    //get round trip departure date here
    val roundDepartureDatePickerListener: OnDateSetListener
        get() = OnDateSetListener { datePicker, startYear, startMonth, startDay ->
            tempYear = startYear
            tempMonth = startMonth
            tempDay = startDay

            //get round trip departure date here
            roundDepartureDate = startYear.toString() + "-" + (startMonth + 1) + "-" + startDay
            btnRoundDepartureDatePicker!!.text =
                HelperUtilities.formatDate(startYear, startMonth, startDay)
        }

    //get round trip return date here
    val roundReturnDatePickerListener: OnDateSetListener
        get() = OnDateSetListener { datePicker, startYear, startMonth, startDay ->
            val departureDate = tempYear.toString() + "-" + (tempMonth + 1) + "-" + tempDay
            val returnDate = startYear.toString() + "-" + (startMonth + 1) + "-" + startDay
            if (HelperUtilities.compareDate(departureDate, returnDate)) {
                datePickerAlert().show()
                isValidRoundDate = false
            } else {
                isValidRoundDate = true
                //get round trip return date here
                roundReturnDate = startYear.toString() + "-" + (startMonth + 1) + "-" + startDay
                btnRoundReturnDatePicker!!.text =
                    HelperUtilities.formatDate(startYear, startMonth, startDay)
            }
        }

    fun datePickerAlert(): Dialog {
        return AlertDialog.Builder(this)
            .setMessage("Please select a valid return date. The return date cannot be before the departure date.")
            .setCancelable(false)
            .setPositiveButton("Ok") { dialog, id -> }.create()
    }

    fun datePickerOneAlert(): Dialog {
        return AlertDialog.Builder(this)
            .setMessage("Please select a departure date.")
            .setCancelable(false)
            .setPositiveButton("Ok") { dialog, id -> }.create()
    }

    fun datePickerTwoAlert(): Dialog {
        return AlertDialog.Builder(this)
            .setMessage("Please select a return date.")
            .setCancelable(false)
            .setPositiveButton("Ok") { dialog, id -> }.create()
    }

    fun searchOneWayFlight() {
        intent = Intent(applicationContext, OneWayFlightListActivity::class.java)
        val sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE)
        applicationContext.getSharedPreferences("PREFS", 0).edit().clear().commit()
        val editor = sharedPreferences.edit()
        editor.putInt("CURRENT_TAB", currentTab)
        editor.putString(
            "ORIGIN",
            HelperUtilities.filter(txtOneWayFrom!!.text.toString().trim { it <= ' ' })
        )
        editor.putString(
            "DESTINATION",
            HelperUtilities.filter(txtOneWayTo!!.text.toString().trim { it <= ' ' })
        )
        editor.putString("DEPARTURE_DATE", oneWayDepartureDate)
        editor.putString("FLIGHT_CLASS", btnOneWayClass!!.text.toString())
        editor.putInt("ONEWAY_NUM_TRAVELLER", oneWayTravellerCount)
        editor.commit()
        startActivity(intent)
    }

    fun searchRoundFlight() {
        intent = Intent(applicationContext, OutboundFlightListActivity::class.java)
        val sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE)
        applicationContext.getSharedPreferences("PREFS", 0).edit().clear().commit()
        val editor = sharedPreferences.edit()
        editor.putInt("CURRENT_TAB", currentTab)
        editor.putString(
            "ORIGIN",
            HelperUtilities.filter(txtRoundFrom!!.text.toString().trim { it <= ' ' })
        )
        editor.putString(
            "DESTINATION",
            HelperUtilities.filter(txtRoundTo!!.text.toString().trim { it <= ' ' })
        )
        editor.putString("DEPARTURE_DATE", roundDepartureDate)
        editor.putString("RETURN_DATE", roundReturnDate)
        editor.putString("FLIGHT_CLASS", btnOneWayClass!!.text.toString())
        editor.putInt("ROUND_NUM_TRAVELLER", roundTravellerCount)
        editor.commit()
        startActivity(intent)
    }

    fun drawerProfileInfo() {
        try {
            val profileName = header!!.findViewById<View>(R.id.profileName) as TextView
            val profileEmail = header!!.findViewById<View>(R.id.profileEmail) as TextView
            databaseHelper = DatabaseHelper(applicationContext)
            db = databaseHelper!!.getReadableDatabase()
            cursor = DatabaseHelper.Companion.selectClientJoinAccount(db, clientID)
            if (cursor != null && cursor!!.count > 0) {
                cursor!!.moveToFirst()
                val fName = cursor!!.getString(0)
                val lName = cursor!!.getString(1)
                val email = cursor!!.getString(4)
                val fullName = "$fName $lName"
                profileName.text = fullName
                profileEmail.text = email
            }
        } catch (ex: SQLiteException) {
            Toast.makeText(applicationContext, "Database unavailable", Toast.LENGTH_SHORT).show()
        }
    }

    //loads image on create
    fun loadImage(clientID: Int) {
        try {
            databaseHelper = DatabaseHelper(applicationContext)
            db = databaseHelper!!.getReadableDatabase()
            cursor = DatabaseHelper.Companion.selectImage(db, clientID)
            if (cursor != null && cursor!!.count > 0) {
                cursor!!.moveToFirst()

                // Create a bitmap from the byte array
                if (cursor!!.getBlob(0) != null) {
                    val image = cursor!!.getBlob(0)
                    imgProfile!!.setImageBitmap(
                        HelperUtilities.decodeSampledBitmapFromByteArray(
                            image,
                            300,
                            300
                        )
                    )
                }
            }
        } catch (ex: SQLiteException) {
            Toast.makeText(applicationContext, "Database unavailable", Toast.LENGTH_SHORT).show()
        }
    }

    fun clientID(): Int {
        val sharedPreferences = getSharedPreferences(LoginActivity.MY_PREFERENCES, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(LoginActivity.CLIENT_ID, 0)
    }

    //validates user input
    val isValidOneWayInput: Boolean
        get() {
            if (HelperUtilities.isEmptyOrNull(txtOneWayFrom!!.text.toString())) {
                txtOneWayFrom!!.error = "Please enter the origin"
                return false
            } else if (!HelperUtilities.isString(txtOneWayFrom!!.text.toString())) {
                txtOneWayFrom!!.error = "Please enter a valid origin"
                return false
            }
            if (HelperUtilities.isEmptyOrNull(txtOneWayTo!!.text.toString())) {
                txtOneWayTo!!.error = "Please enter the destination"
                return false
            } else if (!HelperUtilities.isString(txtOneWayTo!!.text.toString())) {
                txtOneWayTo!!.error = "Please enter a valid destination"
                return false
            }
            if (btnOneWayDepartureDatePicker!!.text.toString()
                    .equals("departure date", ignoreCase = true)
            ) {
                datePickerOneAlert().show()
                return false
            }
            return true
        }

    //validates user input
    val isValidRoundInput: Boolean
        get() {
            if (HelperUtilities.isEmptyOrNull(txtRoundFrom!!.text.toString())) {
                txtRoundFrom!!.error = "Please enter the origin"
                return false
            } else if (!HelperUtilities.isString(txtRoundFrom!!.text.toString())) {
                txtRoundFrom!!.error = "Please enter a valid origin"
                return false
            }
            if (HelperUtilities.isEmptyOrNull(txtRoundTo!!.text.toString())) {
                txtRoundTo!!.error = "Please enter the destination"
                return false
            } else if (!HelperUtilities.isString(txtRoundTo!!.text.toString())) {
                txtRoundTo!!.error = "Please enter a valid destination"
                return false
            }
            if (btnRoundDepartureDatePicker!!.text.toString()
                    .equals("departure date", ignoreCase = true)
            ) {
                datePickerOneAlert().show()
                return false
            }
            if (btnRoundReturnDatePicker!!.text.toString()
                    .equals("return date", ignoreCase = true)
            ) {
                datePickerTwoAlert().show()
                return false
            }
            return true
        }
}