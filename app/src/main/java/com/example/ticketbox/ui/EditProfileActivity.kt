package com.example.ticketbox.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ticketbox.HelperUtils.HelperUtilities
import com.example.ticketbox.R
import com.example.ticketbox.database.DatabaseHelper

class EditProfileActivity : AppCompatActivity() {
    private lateinit var btnUpdateProfile: Button
    private lateinit var clientFirstName: EditText
    private lateinit var clientLastName: EditText
    private lateinit var clientEmail: EditText
    private lateinit var clientPhone: EditText
    private lateinit var clientCreditCard: EditText
    private var clientID = 0
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var db: SQLiteDatabase
    private var cursor: Cursor? = null
    private var isValid = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        btnUpdateProfile = findViewById(R.id.btnSaveProfile)
        clientFirstName = findViewById(R.id.txtFirstNameEdit)
        clientLastName = findViewById(R.id.txtLastNameEdit)
        clientEmail = findViewById(R.id.txtEmailEdit)
        clientPhone = findViewById(R.id.txtPhoneEdit)
        clientCreditCard = findViewById(R.id.txtCreditCardEdit)

        loadProfileInfo()

        btnUpdateProfile.setOnClickListener { updateProfile() }
    }

    private fun loadProfileInfo() {
        try {
            clientID = clientID()
            databaseHelper = DatabaseHelper(applicationContext)
            db = databaseHelper.writableDatabase
            cursor = DatabaseHelper.selectClientJoinAccount(db, clientID)
            if (cursor!!.moveToFirst()) {
                val firstName = cursor!!.getString(0)
                val lastName = cursor!!.getString(1)
                val phone = cursor!!.getString(2)
                val creditCard = cursor!!.getString(3)
                val email = cursor!!.getString(4)
                clientFirstName.setText(firstName)
                clientLastName.setText(lastName)
                clientEmail.setText(email)
                clientPhone.setText(phone)
                clientCreditCard.setText(creditCard)
            }
        } catch (ex: SQLiteException) {
            Toast.makeText(applicationContext, "Database unavailable", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateProfile() {
        try {
            databaseHelper = DatabaseHelper(applicationContext)
            db = databaseHelper.writableDatabase
            clientID = clientID()
            isValid = isValidUserInput
            if (isValid) {
                DatabaseHelper.updateClient(
                    db,
                    clientFirstName.text.toString(),
                    clientLastName.text.toString(),
                    clientPhone.text.toString(),
                    clientCreditCard.text.toString(),
                    clientID
                )
                DatabaseHelper.updateAccount(db, clientEmail.text.toString(), clientID)
                updateProfileDialog().show()
            }
        } catch (ex: SQLiteException) {
            Toast.makeText(applicationContext, "Database unavailable", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clientID(): Int {
        val sharedPreferences = getSharedPreferences(LoginActivity.MY_PREFERENCES, MODE_PRIVATE)
        clientID = sharedPreferences.getInt(LoginActivity.CLIENT_ID, 0)
        return clientID
    }

    private fun updateProfileDialog(): Dialog {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("The profile updated successfully! ")
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, id ->
                val intent = Intent(applicationContext, ProfileActivity::class.java)
                startActivity(intent)
            }
        return builder.create()
    }

    private val isValidUserInput: Boolean
        get() {
            if (HelperUtilities.isEmptyOrNull(clientFirstName.text.toString())) {
                clientFirstName.error = "Please enter your first name"
                return false
            } else if (!HelperUtilities.isString(clientFirstName.text.toString())) {
                clientFirstName.error = "Please enter a valid first name"
                return false
            }
            if (HelperUtilities.isEmptyOrNull(clientLastName.text.toString())) {
                clientLastName.error = "Please enter your last name"
                return false
            } else if (!HelperUtilities.isString(clientLastName.text.toString())) {
                clientLastName.error = "Please enter a valid last name"
                return false
            }
            if (HelperUtilities.isEmptyOrNull(clientEmail.text.toString())) {
                clientEmail.error = "Please enter your email"
                return false
            } else if (!HelperUtilities.isValidEmail(clientEmail.text.toString())) {
                clientEmail.error = "Please enter a valid email"
                return false
            }
            if (HelperUtilities.isEmptyOrNull(clientPhone.text.toString())) {
                clientPhone.error = "Please enter your phone"
                return false
            } else if (!HelperUtilities.isValidPhone(clientPhone.text.toString())) {
                clientPhone.error = "Please enter a valid phone"
                return false
            }
            if (HelperUtilities.isEmptyOrNull(clientCreditCard.text.toString())) {
                clientCreditCard.error = "Please enter your credit card number"
                return false
            }
            return true
        }

    override fun onDestroy() {
        super.onDestroy()
        try {
            cursor?.close()
            db.close()
        } catch (ex: Exception) {
            Toast.makeText(
                applicationContext,
                "Error closing database or cursor",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
