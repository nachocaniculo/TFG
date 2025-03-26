package com.astradevelop.playconnect

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Get the userID token to check if he is logged in or not
        val sharedPref = getSharedPreferences("playconnectlogintoken", Context.MODE_PRIVATE)
        val userUID = sharedPref.getString("userUID", "")
        val nightMode = sharedPref.getBoolean("nightMode", true)

        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        if (userUID != ""){
            val intent = Intent(this, HomeActivity::class.java)
            finish()
            startActivity(intent)
        }

        //Button to go to login activity
        val loginBtn: LinearLayout = findViewById(R.id.loginBtn)
        loginBtn.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        //Button to go to signup activity
        val signupBtn: LinearLayout = findViewById(R.id.signUpBtn)
        signupBtn.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }
}