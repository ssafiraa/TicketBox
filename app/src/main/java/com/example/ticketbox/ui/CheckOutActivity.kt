package com.example.ticketbox.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ticketbox.R

class CheckOutActivity : AppCompatActivity() {
    private var sharedPreferences: SharedPreferences? = null
    private var currentTab = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_out)
        val sharedPreferences = getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        currentTab = sharedPreferences.getInt("CURRENT_TAB", 0)

        //Toast.makeText(getApplicationContext(), String.valueOf(currentTab), Toast.LENGTH_SHORT).show();
        if (currentTab == 0) {
            // Create new fragment and transaction
            val newFragment = CheckoutOnewayFragment()
            val transaction = supportFragmentManager.beginTransaction()

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack
            transaction.replace(R.id.fragment_container, newFragment)
            transaction.addToBackStack(null)

            // Commit the transaction
            transaction.commit()
        } else if (currentTab == 1) {
            // Create new fragment and transaction
            val newFragment = CheckoutRoundFragment()
            val transaction = supportFragmentManager.beginTransaction()

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack
            transaction.replace(R.id.fragment_container, newFragment)
            transaction.addToBackStack(null)

            // Commit the transaction
            transaction.commit()
        }
    }
}