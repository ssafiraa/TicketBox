package com.example.ticketbox.ui

import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ticketbox.HelperUtils.HelperUtilities
import com.example.ticketbox.R
import com.example.ticketbox.database.DatabaseHelper

class LoginActivity : AppCompatActivity() {

    private var inputEmail: EditText? = null
    private var inputPassword: EditText? = null
    private var txtLoginError: TextView? = null
    private var isValid = false
    private var databaseHelper: SQLiteOpenHelper? = null
    private var db: SQLiteDatabase? = null
    private var cursor: Cursor? = null
    private val accountID = 0
    private var clientID = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val sharedPreferences: SharedPreferences = getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE)
        val loggedIn = sharedPreferences.getBoolean(LOGIN_STATUS, false) //login status

        //checks the login status and redirects to the main activity
        if (loggedIn) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
        val btnLogin = findViewById<View>(R.id.btnLogin) as Button
        val linkRegister = findViewById<View>(R.id.linkRegister) as TextView
        inputEmail = findViewById<View>(R.id.email) as EditText
        inputPassword = findViewById<View>(R.id.password) as EditText
        txtLoginError = findViewById<View>(R.id.txtLoginError) as TextView
        btnLogin.setOnClickListener { attemptLogin() }
        linkRegister.setOnClickListener {
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    //process login
    fun attemptLogin() {
        try {
            databaseHelper = DatabaseHelper(this)
            db = (databaseHelper as DatabaseHelper).readableDatabase
            isValid = isValidUserInput

            //filters the user input
            val filteredEmail = HelperUtilities.filter(inputEmail!!.text.toString())
            val filteredPassword = HelperUtilities.filter(inputPassword!!.text.toString())
            if (isValid) {
                cursor = DatabaseHelper.Companion.login(db, filteredEmail, filteredPassword)
                if (cursor != null && cursor!!.count == 1) {
                    cursor!!.moveToFirst()
                    val email = cursor!!.getString(1)
                    clientID = cursor!!.getInt(3)

                    //Toast.makeText(getApplicationContext(), "client id " + String.valueOf(clientID), Toast.LENGTH_SHORT).show();
                    val sharedPreferences = getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putInt(CLIENT_ID, clientID)
                    editor.putString(EMAIL, email)
                    editor.putBoolean(LOGIN_STATUS, true)
                    editor.apply()
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    txtLoginError!!.text = "Invalid email or password"
                }
            }
        } catch (ex: SQLiteException) {
            Toast.makeText(applicationContext, "Database unavailable", Toast.LENGTH_SHORT).show()
        }
    }

    //validate user input
    val isValidUserInput: Boolean
        get() {
            if (HelperUtilities.isEmptyOrNull(inputEmail!!.text.toString())) {
                txtLoginError!!.text = "Invalid email or password"
                return false
            }
            if (HelperUtilities.isEmptyOrNull(inputPassword!!.text.toString())) {
                txtLoginError!!.text = "Invalid email or password"
                return false
            }
            return true
        }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (cursor != null) {
                cursor!!.close()
            }
            if (db != null) {
                db!!.close()
            }
        } catch (ex: Exception) {
            Toast.makeText(
                applicationContext,
                "Error closing database or cursor",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(inState: Bundle) {
        super.onRestoreInstanceState(inState)
    }

    companion object {
        const val MY_PREFERENCES = "my_preferences"
        const val CLIENT_ID = "client_id"
        const val EMAIL = "email"
        const val LOGIN_STATUS = "login_status"
    }
}