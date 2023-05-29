package com.example.ticketbox.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
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

class RegisterActivity : AppCompatActivity() {
    private val clientID = 0
    private var inputFirstName: EditText? = null
    private var inputLastName: EditText? = null
    private var inputEmail: EditText? = null
    private var inputCreditCard: EditText? = null
    private var inputPhone: EditText? = null
    private var inputConfirmPassword: EditText? = null
    private var inputPassword: EditText? = null
    private var isValid = false
    private var hospitalDatabaseHelper: SQLiteOpenHelper? = null
    private var db: SQLiteDatabase? = null
    private var cursor: Cursor? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val register = findViewById<View>(R.id.btnRegister) as Button
        val linkLogin = findViewById<View>(R.id.linkLogin) as TextView
        inputFirstName = findViewById<View>(R.id.txtFirstName) as EditText
        inputLastName = findViewById<View>(R.id.txtLastName) as EditText
        inputEmail = findViewById<View>(R.id.txtEmail) as EditText
        inputPhone = findViewById<View>(R.id.txtPhone) as EditText
        inputCreditCard = findViewById<View>(R.id.txtCreditCard) as EditText
        inputPassword = findViewById<View>(R.id.txtPassword) as EditText
        inputConfirmPassword = findViewById<View>(R.id.txtConfirmPassword) as EditText
        register.setOnClickListener { registerEmployee() }
        linkLogin.setOnClickListener {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    //registers new employee
    fun registerEmployee() {
        try {
            hospitalDatabaseHelper = DatabaseHelper(applicationContext)
            db = hospitalDatabaseHelper!!.getWritableDatabase()
            cursor = DatabaseHelper.Companion.selectAccount(
                db,
                HelperUtilities.filter(inputEmail!!.text.toString())
            )
            isValid = isValidUserInput
            if (isValid) {
                if (cursor != null && cursor!!.count > 0) {
                    accountExistsAlert().show()
                } else {
                    DatabaseHelper.Companion.insertClient(
                        db,
                        inputFirstName!!.text.toString(),
                        inputLastName!!.text.toString(),
                        inputPhone!!.text.toString(),
                        inputCreditCard!!.text.toString()
                    )
                    cursor = DatabaseHelper.Companion.selectClientID(
                        db,
                        inputFirstName!!.text.toString(),
                        inputLastName!!.text.toString(),
                        inputPhone!!.text.toString(),
                        inputCreditCard!!.text.toString()
                    )
                    if (cursor != null && cursor!!.count == 1) {
                        cursor!!.moveToFirst()
                        DatabaseHelper.Companion.insertAccount(
                            db,
                            inputEmail!!.text.toString(),
                            inputPassword!!.text.toString(),
                            cursor!!.getInt(0)
                        )
                        registrationSuccessDialog().show()
                    }
                }
            }
        } catch (ex: SQLiteException) {
            Toast.makeText(applicationContext, "Database unavailable", Toast.LENGTH_SHORT).show()
        }
    }

    fun registrationSuccessDialog(): Dialog {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Your profile created successfully! ")
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, id ->
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        return builder.create()
    }

    fun accountExistsAlert(): Dialog {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("An account with this email already exists. Please try again. ")
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, id -> }
        return builder.create()
    }

    //validates user input
    val isValidUserInput: Boolean
        get() {
            if (HelperUtilities.isEmptyOrNull(inputFirstName!!.text.toString())) {
                inputFirstName!!.error = "Please enter your first name"
                return false
            } else if (!HelperUtilities.isString(inputFirstName!!.text.toString())) {
                inputFirstName!!.error = "Please enter a valid first name"
                return false
            }
            if (HelperUtilities.isEmptyOrNull(inputLastName!!.text.toString())) {
                inputLastName!!.error = "Please enter your last name"
                return false
            } else if (!HelperUtilities.isString(inputLastName!!.text.toString())) {
                inputLastName!!.error = "Please enter a valid last name"
                return false
            }
            if (HelperUtilities.isEmptyOrNull(inputEmail!!.text.toString())) {
                inputEmail!!.error = "Please enter your email"
                return false
            } else if (!HelperUtilities.isValidEmail(inputEmail!!.text.toString())) {
                inputEmail!!.error = "Please enter a valid email"
                return false
            }
            if (HelperUtilities.isEmptyOrNull(inputPhone!!.text.toString())) {
                inputPhone!!.error = "Please enter your phone"
                return false
            } else if (!HelperUtilities.isValidPhone(inputPhone!!.text.toString())) {
                inputPhone!!.error = "Please enter a valid phone"
                return false
            }
            if (HelperUtilities.isEmptyOrNull(inputCreditCard!!.text.toString())) {
                inputCreditCard!!.error = "Please enter your credit card number"
                return false
            }
            if (HelperUtilities.isEmptyOrNull(inputPassword!!.text.toString())) {
                inputPassword!!.error = "Please enter your password"
                return false
            } else if (HelperUtilities.isShortPassword(inputPassword!!.text.toString())) {
                inputPassword!!.error = "Your password must have at least 6 characters."
                return false
            }
            if (HelperUtilities.isEmptyOrNull(inputConfirmPassword!!.text.toString())) {
                inputConfirmPassword!!.error = "Please confirm password"
                return false
            }
            if (inputConfirmPassword!!.text.toString() != inputPassword!!.text.toString()) {
                inputConfirmPassword!!.error = "The password doesn't match."
                return false
            }
            return true
        }

    override fun onDestroy() {
        super.onDestroy()
        try {
            cursor!!.close()
            db!!.close()
        } catch (ex: Exception) {
            Toast.makeText(
                applicationContext,
                "Error closing database or cursor",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}