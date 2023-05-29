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
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ticketbox.HelperUtils.HelperUtilities
import com.example.ticketbox.R
import com.example.ticketbox.database.DatabaseHelper

class SecurityActivity : AppCompatActivity() {
    private var oldPassword: EditText? = null
    private var newPassword: EditText? = null
    private var confirmPassword: EditText? = null
    private var clientID = 1
    private var txtMatchError: TextView? = null
    private var isValid = false
    private var isValidUser = false
    private var hospitalDatabaseHelper: SQLiteOpenHelper? = null
    private var db: SQLiteDatabase? = null
    private var cursor: Cursor? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security)
        oldPassword = findViewById<View>(R.id.txtOldPassword) as EditText
        newPassword = findViewById<View>(R.id.txtNewPassword) as EditText
        confirmPassword = findViewById<View>(R.id.txtConfirmPassword) as EditText
        txtMatchError = findViewById<View>(R.id.txtMatchError) as TextView
        val btnChangePassword = findViewById<View>(R.id.btnChangePassword) as Button
        clientID = clientID()
        btnChangePassword.setOnClickListener { changePassword() }
    }

    fun changePassword() {
        try {
            hospitalDatabaseHelper = DatabaseHelper(applicationContext)
            db = hospitalDatabaseHelper!!.getWritableDatabase()
            clientID = clientID()
            isValid = isValidInput
            isValidUser = isValidUser()
            if (isValid && isValidUser) {
                DatabaseHelper.Companion.updatePassword(
                    db,
                    newPassword!!.text.toString(), clientID.toString()
                )
                changePasswordDialog().show()
            }
        } catch (ex: SQLiteException) {
            Toast.makeText(applicationContext, "Database unavailable", Toast.LENGTH_SHORT).show()
        }
    }

    fun changePasswordDialog(): Dialog {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("The password changed successfully! ")
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, id ->
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
            }
        return builder.create()
    }

    val isValidInput: Boolean
        get() {
            isValidUser = isValidUser()
            if (HelperUtilities.isEmptyOrNull(oldPassword!!.text.toString())) {
                oldPassword!!.error = "Please enter your password"
                return false
            } else if (!isValidUser) {
                oldPassword!!.error = "Incorrect password"
                return false
            } else if (HelperUtilities.isEmptyOrNull(newPassword!!.text.toString())) {
                newPassword!!.error = "Please enter your new password"
                return false
            } else if (HelperUtilities.isEmptyOrNull(confirmPassword!!.text.toString())) {
                confirmPassword!!.error = "Please confirm your new password"
                return false
            } else if (HelperUtilities.isShortPassword(newPassword!!.text.toString())) {
                newPassword!!.error = "Your password must have at least 6 characters."
                return false
            } else if (newPassword!!.text.toString() != confirmPassword!!.text.toString()) {
                txtMatchError!!.text = "The password doesn't match"
                return false
            }
            return true
        }

    fun isValidUser(): Boolean {
        try {
            hospitalDatabaseHelper = DatabaseHelper(applicationContext)
            db = hospitalDatabaseHelper!!.getReadableDatabase()
            cursor = DatabaseHelper.Companion.selectClientPassword(db, clientID)
            if (cursor!!.moveToFirst()) {
                val password = cursor!!.getString(0)
                return if (oldPassword!!.text.toString() != password) {
                    false
                } else {
                    true
                }
            }
        } catch (ex: SQLiteException) {
            Toast.makeText(applicationContext, "Database unavailable", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    fun clientID(): Int {
        val sharedPreferences = getSharedPreferences(LoginActivity.MY_PREFERENCES, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(LoginActivity.CLIENT_ID, 0)
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
}