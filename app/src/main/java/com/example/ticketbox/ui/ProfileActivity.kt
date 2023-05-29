package com.example.ticketbox.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ticketbox.HelperUtils.HelperUtilities
import com.example.ticketbox.R
import com.example.ticketbox.database.DatabaseHelper
import com.example.ticketbox.models.Account
import com.example.ticketbox.models.Client
import java.io.ByteArrayOutputStream

class ProfileActivity : AppCompatActivity() {
    private var intent: Intent? = null
    private var clientID = 0
    private val TAG: String? = null
    private var uploadImage: ImageButton? = null
    private var profileImage: ImageView? = null
    private var databaseHelper: SQLiteOpenHelper? = null
    private var db: SQLiteDatabase? = null
    private var cursor: Cursor? = null
    private var clientFirstname: TextView? = null
    private var clientLastName: TextView? = null
    private var clientEmail: TextView? = null
    private var clientPhone: TextView? = null
    private var fullName: TextView? = null
    private var clientCreditCard: TextView? = null
    private var editProfile: ImageButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        profileImage = findViewById<View>(R.id.profileImage) as ImageView
        uploadImage = findViewById<View>(R.id.btnEditProfilePicture) as ImageButton
        editProfile = findViewById<View>(R.id.btnEditProfile) as ImageButton
        clientID = clientID()
        getProfileInformation(clientID)
        loadImage(clientID)
        uploadImage!!.setOnClickListener {
            val uploadImageIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(uploadImageIntent, REQUEST_CODE)
        }
        editProfile!!.setOnClickListener {
            intent = Intent(applicationContext, EditProfileActivity::class.java)
            startActivity(intent)
        }
    }

    //gets employee profile information
    @SuppressLint("SetTextI18n")
    fun getProfileInformation(employeeID: Int) {
        try {
            clientFirstname = findViewById<View>(R.id.txtClientFirstName) as TextView
            clientLastName = findViewById<View>(R.id.txtClientLastName) as TextView
            clientEmail = findViewById<View>(R.id.txtClientEmail) as TextView
            clientPhone = findViewById<View>(R.id.txtClientPhone) as TextView
            clientCreditCard = findViewById<View>(R.id.txtClientCreditCard) as TextView
            fullName = findViewById<View>(R.id.txtFullName) as TextView
            databaseHelper = DatabaseHelper(applicationContext)
            db = databaseHelper!!.getReadableDatabase()
            cursor = DatabaseHelper.Companion.selectClientJoinAccount(db, clientID)
            if (cursor != null && cursor!!.count > 0) {
                cursor!!.moveToFirst()
                val fName = cursor!!.getString(0)
                val lName = cursor!!.getString(1)
                val phone = cursor!!.getString(2)
                val creditCard = cursor!!.getString(3)
                val email = cursor!!.getString(4)
                val client = Client(fName, lName, phone, creditCard)
                val account = Account(email)
                clientFirstname!!.text = "First Name: " + client.firstName
                clientLastName!!.text = "Last Name: " + client.lastName
                clientPhone!!.text = "Phone: " + client.phone
                clientCreditCard!!.text =
                    "Credit Card: " + (client.creditCard)
                clientEmail!!.text = "Email: " + account.email
                fullName!!.setText(client.firstName + " " + client.lastName)
            }
        } catch (ex: SQLiteException) {
            Toast.makeText(applicationContext, "Database unavailable", Toast.LENGTH_SHORT).show()
        }
    }

    fun clientID(): Int {
        val sharedPreferences = getSharedPreferences(LoginActivity.MY_PREFERENCES, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(LoginActivity.CLIENT_ID, 0)
    }

    //uploads image from sd card
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            when (requestCode) {
                REQUEST_CODE -> if (resultCode == RESULT_OK && data != null) {

                    //data gives you the image uri. Try to convert that to bitmap
                    val selectedImage = data.data

                    //uploadImage.setImageURI(selectedImage);
                    val bitmap =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)

                    // Create a byte array from ByteArrayOutputStream
                    val byteArray = stream.toByteArray()
                    try {
                        databaseHelper = DatabaseHelper(applicationContext)
                        db = databaseHelper!!.getWritableDatabase()
                        DatabaseHelper.Companion.updateClientImage(
                            db,
                            byteArray, clientID.toString()
                        )
                        db = databaseHelper!!.getReadableDatabase()
                        cursor = DatabaseHelper.Companion.selectImage(db, clientID)
                        if (cursor!!.moveToFirst()) {
                            // Create a bitmap from the byte array
                            val image = cursor!!.getBlob(0)
                            profileImage!!.setImageBitmap(
                                HelperUtilities.decodeSampledBitmapFromByteArray(
                                    image,
                                    300,
                                    300
                                )
                            )
                        }
                    } catch (ex: SQLiteException) {
                        Toast.makeText(
                            applicationContext,
                            "Database unavailable",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    Log.e(TAG, "Selecting picture cancelled")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in onActivityResult : " + e.message)
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
                    profileImage!!.setImageBitmap(
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

    companion object {
        private const val REQUEST_CODE = 1
    }
}