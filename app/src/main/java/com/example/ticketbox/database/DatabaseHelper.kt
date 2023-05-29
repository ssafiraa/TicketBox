package com.example.ticketbox.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.ticketbox.HelperUtils.HelperUtilities
import java.util.Locale

class DatabaseHelper(context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        updateDatabase(db, 0, DB_VERSION)
    }

    //requires API level 16 and above
    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        if (!db.isReadOnly) {
            db.setForeignKeyConstraintsEnabled(true)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + "AIRLINE")
        db.execSQL("DROP TABLE IF EXISTS " + "FLIGHT")
        db.execSQL("DROP TABLE IF EXISTS " + "SEAT")
        db.execSQL("DROP TABLE IF EXISTS " + "FLIGHTCLASS")
        db.execSQL("DROP TABLE IF EXISTS " + "CLIENT")
        db.execSQL("DROP TABLE IF EXISTS " + "ACCOUNT")
        db.execSQL("DROP TABLE IF EXISTS " + "ITINERARY")
        updateDatabase(db, oldVersion, newVersion)
    }

    private fun updateDatabase(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 1) {

            //create tables here
            db.execSQL(createAirline())
            db.execSQL(createFlight())
            db.execSQL(createSeat())
            db.execSQL(createFlightClass())
            db.execSQL(createClient())
            db.execSQL(createAccount())
            db.execSQL(createItinerary())

            //insert default data fro testing
            insertAirline(db, "Air Canada")
            insertAirline(db, "Air France")
            insertAirline(db, "Air Transat")
            insertAirline(db, "Alitalia")
            insertAirline(db, "Austrian")
            insertAirline(db, "Delta")
            insertAirline(db, "Emirates")
            insertAirline(db, "InterJet")
            insertAirline(db, "Lufthansa")
            insertAirline(db, "United")
            insertAirline(db, "WestJet")


            //Jakarta to Surabaya
            insertFlight(
                db,
                "Jakarta",
                "Surabaya",
                "2023-6-10",
                "2023-6-10",
                "10:10",
                "12:10",
                200.00,
                1
            )
            insertFlight(
                db,
                "Jakarta",
                "Surabaya",
                "2023-6-10",
                "2023-6-10",
                "10:10",
                "12:10",
                150.00,
                2
            )
            insertFlight(
                db,
                "Jakarta",
                "Surabaya",
                "2023-6-10",
                "2023-6-10",
                "11:10",
                "12:10",
                350.00,
                3
            )
            insertFlight(
                db,
                "Jakarta",
                "Surabaya",
                "2023-6-10",
                "2023-6-10",
                "09:10",
                "12:10",
                250.00,
                4
            )
            insertFlight(
                db,
                "Jakarta",
                "Surabaya",
                "2023-6-10",
                "2023-6-10",
                "10:10",
                "12:10",
                100.00,
                5
            )

            //Surabaya to Jakarta
            insertFlight(
                db,
                "Surabaya",
                "Jakarta",
                "2023-6-12",
                "2023-6-12",
                "10:10",
                "12:10",
                120.00,
                6
            )
            insertFlight(
                db,
                "Surabaya",
                "Jakarta",
                "2023-6-12",
                "2023-6-12",
                "09:00",
                "12:10",
                150.00,
                7
            )
            insertFlight(
                db,
                "Surabaya",
                "Jakarta",
                "2023-6-12",
                "2023-6-12",
                "10:10",
                "12:10",
                170.00,
                6
            )
            insertFlight(
                db,
                "Surabaya",
                "Jakarta",
                "2023-6-12",
                "2023-6-12",
                "09:00",
                "12:10",
                140.00,
                9
            )
            insertFlight(
                db,
                "Surabaya",
                "Jakarta",
                "2023-6-12",
                "2023-6-12",
                "09:00",
                "12:10",
                100.00,
                10
            )
            insertFlight(
                db,
                "Surabaya",
                "Jakarta",
                "2023-6-12",
                "2023-6-12",
                "10:10",
                "12:10",
                350.00,
                11
            )

            //Bali to Yogyakarta
            insertFlight(
                db,
                "Bali",
                "Yogyakarta",
                "2023-6-25",
                "2023-6-25",
                "02:00",
                "04:45",
                300.00,
                2
            )
            insertFlight(
                db,
                "Bali",
                "Yogyakarta",
                "2023-6-25",
                "2023-6-25",
                "01:00",
                "03:15",
                205.00,
                1
            )
            insertFlight(
                db,
                "Bali",
                "Yogyakarta",
                "2023-6-25",
                "2023-6-25",
                "09:00",
                "12:20",
                350.00,
                10
            )
            insertFlight(
                db,
                "Bali",
                "Yogyakarta",
                "2023-6-25",
                "2023-6-25",
                "06:00",
                "10:15",
                400.00,
                11
            )
            insertFlight(
                db,
                "Bali",
                "Yogyakarta",
                "2023-6-25",
                "2023-6-25",
                "11:00",
                "13:11",
                250.00,
                1
            )

            //Yogyakarta to Bali
            insertFlight(
                db,
                "Yogyakarta",
                "Bali",
                "2023-9-15",
                "2023-9-15",
                "11:00",
                "13:11",
                300.00,
                9
            )
            insertFlight(
                db,
                "Yogyakarta",
                "Bali",
                "2023-9-15",
                "2023-9-15",
                "09:00",
                "12:00",
                250.00,
                1
            )
            insertFlight(
                db,
                "Yogyakarta",
                "Bali",
                "2023-9-15",
                "2023-9-15",
                "06:00",
                "11:00",
                400.00,
                3
            )
            insertFlight(
                db,
                "Yogyakarta",
                "Bali",
                "2023-9-15",
                "2023-9-15",
                "01:00",
                "04:00",
                150.00,
                7
            )
            insertFlight(
                db,
                "Yogyakarta",
                "Bali",
                "2023-9-15",
                "2023-9-15",
                "12:00",
                "14:00",
                350.00,
                10
            )
            insertFlight(
                db,
                "Montreal",
                "Bali",
                "2023-7-26",
                "2023-7-26",
                "10:10",
                "12:10",
                350.00,
                4
            )
            insertFlight(
                db,
                "New York",
                "Bali",
                "2023-6-15",
                "2023-6-15",
                "09:10",
                "12:10",
                165.00,
                5
            )
            insertFlight(
                db,
                "Quebec City",
                "NewYork",
                "2023-7-26",
                "2023-7-26",
                "11:10",
                "12:10",
                250.00,
                6
            )
            insertFlight(
                db,
                "Charlottetown",
                "Victoria",
                "2023-6-25",
                "2023-6-25",
                "10:10",
                "12:10",
                360.00,
                7
            )
            insertFlight(
                db,
                "Los Angeles",
                "Surabaya",
                "2023-6-26",
                "2023-6-26",
                "10:10",
                "12:10",
                350.00,
                6
            )
            insertFlight(
                db,
                "Yogyakarta",
                "Jakarta",
                "2023-6-27",
                "2023-6-27",
                "09:10",
                "12:10",
                350.00,
                9
            )
            insertFlight(
                db,
                "Victoria",
                "New York",
                "2023-6-26",
                "2023-6-26",
                "10:10",
                "12:10",
                350.00,
                10
            )
            insertSeat(db, 0, 1, 1)
            insertSeat(db, 0, 2, 1)
            insertSeat(db, 0, 3, 1)
            insertSeat(db, 0, 4, 1)
            insertSeat(db, 0, 5, 1)
            insertSeat(db, 0, 6, 1)
            insertSeat(db, 0, 7, 1)
            insertSeat(db, 0, 6, 1)
            insertSeat(db, 0, 9, 1)
            insertSeat(db, 0, 10, 1)
            insertSeat(db, 0, 11, 1)
            insertSeat(db, 0, 12, 1)
            insertSeat(db, 0, 13, 1)
            insertSeat(db, 0, 14, 1)
            insertSeat(db, 0, 15, 1)
            insertSeat(db, 0, 16, 1)
            insertSeat(db, 0, 17, 1)
            insertSeat(db, 0, 16, 1)
            insertSeat(db, 0, 19, 1)
            insertSeat(db, 0, 20, 1)
            insertSeat(db, 0, 21, 1)
            insertSeat(db, 0, 22, 1)
            insertSeat(db, 0, 23, 1)
            insertSeat(db, 0, 24, 1)
            insertSeat(db, 0, 25, 1)
            insertSeat(db, 0, 26, 2)
            insertSeat(db, 0, 27, 2)
            insertSeat(db, 0, 26, 2)
            insertFlightClass(db, "Economy")
            insertFlightClass(db, "Business")
            insertClient(db, "John", "Doe", "4164121000", "5412547654125963")
            insertAccount(db, "john@gmail.com", "password", 1)
            db.execSQL(updateFlight())
            db.execSQL(updateSeatNumber())
        }
    }

    fun createAirline(): String {
        return ("CREATE TABLE AIRLINE ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "AIRLINENAME TEXT COLLATE NOCASE);")
    }

    fun createFlight(): String {
        return "CREATE TABLE FLIGHT (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "FLIGHTNUMBER INTEGER, " +
                "ORIGIN TEXT COLLATE NOCASE, " +
                "DESTINATION TEXT COLLATE NOCASE, " +
                "DEPARTUREDATE DATE, " +
                "ARRIVALDATE DATE, " +
                "DEPARTURETIME TIME, " +
                "ARRIVALTIME TIME, " +
                "FLIGHTDURATION TIME, " +
                "FARE REAL, " +
                "FLIGHT_AIRLINE INTEGER, " +
                "FOREIGN KEY(FLIGHT_AIRLINE) REFERENCES AIRLINE(_id));"
    }

    fun createSeat(): String {
        return "CREATE TABLE SEAT (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "SEATNUMBER INTEGER, " +
                "SEAT_FLIGHT INTEGER, " +
                "STATUS INTEGER, " +
                "SEAT_FLIGHTCLASS INTEGER, " +
                "FOREIGN KEY(SEAT_FLIGHT) REFERENCES FLIGHT(_id)," +
                "FOREIGN KEY(SEAT_FLIGHTCLASS) REFERENCES FLIGHTCLASS(_id));"
    }

    fun createFlightClass(): String {
        return "CREATE TABLE FLIGHTCLASS (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "FLIGHTCLASSNAME TEXT);"
    }

    fun createClient(): String {
        return "CREATE TABLE CLIENT (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "FIRSTNAME TEXT COLLATE NOCASE, " +
                "LASTNAME TEXT COLLATE NOCASE, " +
                "PHONE TEXT, " +
                "CREDITCARD TEXT, " +
                "IMAGE BLOB);"
    }

    fun createAccount(): String {
        return "CREATE TABLE ACCOUNT (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "EMAIL TEXT, " +
                "PASSWORD TEXT, " +
                "ACCOUNT_CLIENT INTEGER, " +
                "FOREIGN KEY (ACCOUNT_CLIENT) REFERENCES CLIENT(_id));"
    }

    fun createItinerary(): String {
        return "CREATE TABLE ITINERARY (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "TIMESTAMP DATETIME DEFAULT (STRFTIME('%Y-%m-%d  %H:%M', 'NOW','localtime')), " +
                "ITINERARY_CLIENT INTEGER, " +
                "ITINERARY_FLIGHT INTEGER, " +
                "TRAVELLER INTEGER, " +
                "FOREIGN KEY(ITINERARY_CLIENT) REFERENCES CLIENT(_id), " +
                "FOREIGN KEY(ITINERARY_FLIGHT) REFERENCES FLIGHT(_id));"
    }

    fun updateFlight(): String {
        return "UPDATE FLIGHT SET FLIGHTDURATION = ((strftime('%s',ARRIVALTIME) - strftime('%s', DEPARTURETIME)) / 60)/60, " +
                "FLIGHTNUMBER = _id + 10000"
    }

    fun updateSeatNumber(): String {
        return "UPDATE SEAT SET SEATNUMBER = _id + 100"
    }

    fun insertAirline(db: SQLiteDatabase, airlineName: String?) {
        val airlineValues = ContentValues()
        airlineValues.put("AIRLINENAME", airlineName)
        db.insert("AIRLINE", null, airlineValues)
    }

    fun insertFlight(
        db: SQLiteDatabase,
        origin: String?,
        destination: String?,
        departureDate: String?,
        arrivalDate: String?,
        departureTime: String?,
        arrivalTime: String?,
        fare: Double?,
        airlineID: Int
    ) {
        val flightValues = ContentValues()
        flightValues.put("ORIGIN", origin)
        flightValues.put("DESTINATION", destination)
        flightValues.put("DEPARTUREDATE", departureDate)
        flightValues.put("ARRIVALDATE", arrivalDate)
        flightValues.put("DEPARTURETIME", departureTime)
        flightValues.put("ARRIVALTIME", arrivalTime)
        flightValues.put("FARE", fare)
        flightValues.put("FLIGHT_AIRLINE", airlineID)
        db.insert("FLIGHT", null, flightValues)
    }

    fun insertSeat(db: SQLiteDatabase, status: Int, flightID: Int, flightClassID: Int) {
        val seatValues = ContentValues()
        seatValues.put("STATUS", status)
        seatValues.put("SEAT_FLIGHT", flightID)
        seatValues.put("SEAT_FLIGHTCLASS", flightClassID)
        db.insert("SEAT", null, seatValues)
    }

    fun insertFlightClass(db: SQLiteDatabase, flightClassName: String?) {
        val flightClassValues = ContentValues()
        flightClassValues.put("FLIGHTCLASSNAME", flightClassName)
        db.insert("FLIGHTCLASS", null, flightClassValues)
    }

    companion object {
        private const val DB_NAME = "TICKETBOX"
        private const val DB_VERSION = 1
        fun insertClient(
            db: SQLiteDatabase?,
            firstName: String,
            lastName: String,
            phone: String?,
            creditCard: String?
        ) {
            val clientValues = ContentValues()
            clientValues.put(
                "FIRSTNAME",
                HelperUtilities.capitalize(firstName.lowercase(Locale.getDefault()))
            )
            clientValues.put(
                "LASTNAME",
                HelperUtilities.capitalize(lastName.lowercase(Locale.getDefault()))
            )
            clientValues.put("PHONE", phone)
            clientValues.put("CREDITCARD", creditCard)
            db!!.insert("CLIENT", null, clientValues)
        }

        fun insertAccount(db: SQLiteDatabase?, email: String?, password: String?, clientID: Int) {
            val accountValues = ContentValues()
            accountValues.put("EMAIL", email)
            accountValues.put("PASSWORD", password)
            accountValues.put("ACCOUNT_CLIENT", clientID)
            db!!.insert("ACCOUNT", null, accountValues)
        }

        fun insertItinerary(db: SQLiteDatabase?, flightID: Int, clientID: Int, traveller: Int) {
            val itineraryValues = ContentValues()
            itineraryValues.put("ITINERARY_FLIGHT", flightID)
            itineraryValues.put("ITINERARY_CLIENT", clientID)
            itineraryValues.put("TRAVELLER", traveller)
            db!!.insert("ITINERARY", null, itineraryValues)
        }

        fun selectFlight(db: SQLiteDatabase?, flightID: Int): Cursor {
            return db!!.rawQuery(
                "SELECT FLIGHT._id, FLIGHTNUMBER, ORIGIN, DESTINATION, DEPARTUREDATE, ARRIVALDATE, DEPARTURETIME, " +
                        " ARRIVALTIME, FLIGHTDURATION, FARE, AIRLINENAME, SEATNUMBER, FLIGHTCLASSNAME " +
                        "FROM FLIGHT " +
                        "INNER JOIN AIRLINE " +
                        "ON FLIGHT.FLIGHT_AIRLINE = AIRLINE._id " +
                        "INNER JOIN " +
                        "SEAT " +
                        "ON SEAT.SEAT_FLIGHT = FLIGHT._id " +
                        "INNER JOIN " +
                        "FLIGHTCLASS " +
                        "ON SEAT.SEAT_FLIGHTCLASS = FLIGHTCLASS._id " +
                        "WHERE FLIGHT._id = '" + flightID + "'", null
            )
        }

        fun getItineraryDetail(db: SQLiteDatabase?, flightID: Int): Cursor {
            return db!!.rawQuery(
                "SELECT FLIGHT._id, FLIGHTNUMBER, ORIGIN, DESTINATION, DEPARTUREDATE, ARRIVALDATE, DEPARTURETIME, " +
                        " ARRIVALTIME, FLIGHTDURATION, FARE, AIRLINENAME, SEATNUMBER, FLIGHTCLASSNAME, TRAVELLER, TIMESTAMP " +
                        "FROM FLIGHT " +
                        "INNER JOIN AIRLINE " +
                        "ON FLIGHT.FLIGHT_AIRLINE = AIRLINE._id " +
                        "INNER JOIN " +
                        "SEAT " +
                        "ON SEAT.SEAT_FLIGHT = FLIGHT._id " +
                        "INNER JOIN " +
                        "FLIGHTCLASS " +
                        "ON SEAT.SEAT_FLIGHTCLASS = FLIGHTCLASS._id " +
                        "JOIN ITINERARY " +
                        "ON ITINERARY.ITINERARY_FLIGHT = FLIGHT._id " +
                        "WHERE FLIGHT._id = '" + flightID + "'", null
            )
        }

        fun selectFlight(
            db: SQLiteDatabase?, origin: String?, destination: String?,
            departureDate: String?, flightClass: String?
        ): Cursor {
            return db!!.rawQuery(
                "SELECT FLIGHT._id, FLIGHTNUMBER, ORIGIN, DESTINATION, DEPARTUREDATE, ARRIVALDATE, DEPARTURETIME, " +
                        " ARRIVALTIME, FLIGHTDURATION, FARE, AIRLINENAME, SEATNUMBER, FLIGHTCLASSNAME " +
                        "FROM FLIGHT " +
                        "JOIN AIRLINE " +
                        "ON AIRLINE._id = FLIGHT.FLIGHT_AIRLINE " +
                        "JOIN " +
                        "SEAT " +
                        "ON FLIGHT._id = SEAT.SEAT_FLIGHT " +
                        "INNER JOIN " +
                        "FLIGHTCLASS " +
                        "ON SEAT.SEAT_FLIGHTCLASS = FLIGHTCLASS._id " +
                        "WHERE ORIGIN = '" + origin +
                        "' AND DESTINATION = '" + destination +
                        "' AND DEPARTUREDATE = '" + departureDate +
                        "' AND FLIGHTCLASSNAME = '" + flightClass +
                        "' AND SEAT.STATUS = 0 ", null
            )
        }

        fun selectFlight(
            db: SQLiteDatabase?, origin: String?, destination: String?,
            departureDate: String?, flightClass: String?, orderBy: String
        ): Cursor {
            return db!!.rawQuery(
                "SELECT FLIGHT._id, FLIGHTNUMBER, ORIGIN, DESTINATION, DEPARTUREDATE, ARRIVALDATE, DEPARTURETIME, " +
                        " ARRIVALTIME, FLIGHTDURATION, FARE, AIRLINENAME, SEATNUMBER, FLIGHTCLASSNAME " +
                        "FROM FLIGHT " +
                        "INNER JOIN AIRLINE " +
                        "ON FLIGHT.FLIGHT_AIRLINE = AIRLINE._id " +
                        "INNER JOIN " +
                        "SEAT " +
                        "ON SEAT.SEAT_FLIGHT = FLIGHT._id " +
                        "INNER JOIN " +
                        "FLIGHTCLASS " +
                        "ON SEAT.SEAT_FLIGHTCLASS = FLIGHTCLASS._id " +
                        "WHERE ORIGIN = '" + origin +
                        "' AND DESTINATION = '" + destination +
                        "' AND DEPARTUREDATE = '" + departureDate +
                        "' AND FLIGHTCLASSNAME = '" + flightClass +
                        "' AND SEAT.STATUS = 0 " +
                        "ORDER BY " + orderBy + " ASC", null
            )
        }

        fun selectItinerary(db: SQLiteDatabase?, clientID: Int): Cursor {
            return db!!.rawQuery(
                "SELECT FLIGHT._id, FLIGHTNUMBER, ORIGIN, DESTINATION, DEPARTUREDATE, ARRIVALDATE, DEPARTURETIME, " +
                        " ARRIVALTIME, FLIGHTDURATION, FARE, AIRLINENAME, SEATNUMBER, FLIGHTCLASSNAME " +
                        "FROM FLIGHT " +
                        "INNER JOIN AIRLINE " +
                        "ON FLIGHT.FLIGHT_AIRLINE = AIRLINE._id " +
                        "INNER JOIN ITINERARY " +
                        "ON  FLIGHT._id = ITINERARY.ITINERARY_FLIGHT " +
                        "INNER JOIN " +
                        "SEAT " +
                        "ON SEAT.SEAT_FLIGHT = FLIGHT._id " +
                        "INNER JOIN " +
                        "FLIGHTCLASS " +
                        "ON SEAT.SEAT_FLIGHTCLASS = FLIGHTCLASS._id " +
                        "WHERE ITINERARY.ITINERARY_CLIENT = " + clientID, null
            )
        }

        fun deleteItinerary(db: SQLiteDatabase?, itineraryID: Int) {
            db!!.delete("ITINERARY", " _id = ? ", arrayOf(itineraryID.toString()))
        }

        fun selectItinerary(db: SQLiteDatabase?, flightID: Int, clientID: Int): Cursor {
            return db!!.query(
                "ITINERARY",
                null,
                " ITINERARY_FLIGHT = ? AND ITINERARY_CLIENT = ?",
                arrayOf(flightID.toString(), clientID.toString()),
                null,
                null,
                null,
                null
            )
        }

        fun login(db: SQLiteDatabase?, email: String?, password: String?): Cursor {
            return db!!.query(
                "ACCOUNT", arrayOf("_id", "EMAIL", "PASSWORD", "ACCOUNT_CLIENT"),
                "EMAIL = ? AND PASSWORD = ? ", arrayOf(email, password),
                null, null, null, null
            )
        }

        fun deleteAccount(db: SQLiteDatabase, clientID: String) {
            db.delete("CLIENT", "_id = ? ", arrayOf(clientID))
            db.delete("ACCOUNT", "_id = ? ", arrayOf(clientID))
            db.delete("ITINERARY", "_id = ? ", arrayOf(clientID))
        }

        fun updateClientImage(db: SQLiteDatabase?, image: ByteArray?, id: String) {
            val employeeValues = ContentValues()
            employeeValues.put("IMAGE", image)
            db!!.update("CLIENT", employeeValues, " _id = ? ", arrayOf(id))
        }

        fun updatePassword(db: SQLiteDatabase?, password: String?, id: String) {
            val clientValues = ContentValues()
            clientValues.put("PASSWORD", password)
            db!!.update("ACCOUNT", clientValues, " _id = ? ", arrayOf(id))
        }

        fun selectImage(db: SQLiteDatabase?, clientID: Int): Cursor {
            return db!!.query(
                "CLIENT",
                arrayOf("IMAGE"),
                "_id = ? ",
                arrayOf(Integer.toString(clientID)),
                null,
                null,
                null,
                null
            )
        }

        fun selectClientPassword(db: SQLiteDatabase?, clientID: Int): Cursor {
            return db!!.query(
                "ACCOUNT",
                arrayOf("PASSWORD"),
                "_id = ? ",
                arrayOf(Integer.toString(clientID)),
                null,
                null,
                null,
                null
            )
        }

        fun updateClient(
            db: SQLiteDatabase?, firstName: String, lastName: String,
            phone: String?, creditCard: String?, clientID: Int
        ) {
            val clientValues = ContentValues()
            clientValues.put(
                "FIRSTNAME",
                HelperUtilities.capitalize(firstName.lowercase(Locale.getDefault()))
            )
            clientValues.put(
                "LASTNAME",
                HelperUtilities.capitalize(lastName.lowercase(Locale.getDefault()))
            )
            clientValues.put("PHONE", phone)
            clientValues.put("CREDITCARD", creditCard)
            db!!.update("CLIENT", clientValues, "_id = ?", arrayOf(clientID.toString()))
        }

        fun updateAccount(db: SQLiteDatabase?, email: String?, clientID: Int) {
            val accountValues = ContentValues()
            accountValues.put("EMAIL", email)
            db!!.update(
                "ACCOUNT",
                accountValues,
                " ACCOUNT_CLIENT = ?",
                arrayOf(clientID.toString())
            )
        }

        fun selectClientID(
            db: SQLiteDatabase?, firstName: String, lastName: String,
            phone: String, creditCard: String
        ): Cursor {
            return db!!.query(
                "CLIENT",
                arrayOf("_id"),
                "FIRSTNAME = ? AND LASTNAME = ? AND PHONE = ? AND CREDITCARD = ? ",
                arrayOf(firstName, lastName, phone, creditCard),
                null,
                null,
                null,
                null
            )
        }

        fun selectClientJoinAccount(db: SQLiteDatabase?, clientID: Int): Cursor {
            return db!!.rawQuery(
                "SELECT FIRSTNAME, LASTNAME, PHONE, CREDITCARD, EMAIL FROM CLIENT " +
                        "JOIN ACCOUNT " +
                        "ON CLIENT._id = ACCOUNT.ACCOUNT_CLIENT " +
                        "WHERE " +
                        "CLIENT._id = '" + clientID + "'", null
            )
        }

        fun selectClient(db: SQLiteDatabase, clientID: Int): Cursor {
            return db.query(
                "CLIENT",
                null,
                " _id = ? ",
                arrayOf(clientID.toString()),
                null,
                null,
                null,
                null
            )
        }

        fun selectAccount(db: SQLiteDatabase?, email: String?): Cursor {
            return db!!.query(
                "ACCOUNT",
                null,
                " EMAIL = ? ",
                arrayOf(email),
                null,
                null,
                null,
                null
            )
        }

        val CITIES = arrayOf(
            "Jakarta",
            "Surabaya",
            "Bali",
            "Yogyakarta",
            "Victoria",
            "Fredericton",
            "St. John's",
            "Halifax",
            "Charlottetown",
            "Quebec City",
            "Regina",
            "Yellowknife",
            "Iqaluit",
            "Whitehorse",
            "New York",
            "Boston",
            "Los Angeles",
            "Montreal"
        )
    }
}