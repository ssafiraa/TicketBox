package com.example.ticketbox.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.ticketbox.R

class LaunchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_launch)
        val myLayout = findViewById<View>(R.id.launch)
        Handler().postDelayed({
            val intent = Intent(this@LaunchActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, TIME_OUT.toLong())
    }

    companion object {
        private const val TIME_OUT = 3000
    }
}