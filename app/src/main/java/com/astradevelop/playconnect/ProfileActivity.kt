package com.astradevelop.playconnect

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ProfileActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Display user info
        val email = intent.extras!!.getString("email")
        var user = intent.extras!!.getString("user")
        val profilePicture = intent.extras!!.getString("profilePicture")

        val userTxt: TextView = findViewById(R.id.userTxt)
        userTxt.text = user

        val mailTxt: TextView = findViewById(R.id.mailText)
        mailTxt.text = email

        //Back button
        val backBtn: ImageView = findViewById(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
        }

        //Logout button handler
        val logoutBtn: LinearLayout = findViewById(R.id.logoutBtn)
        logoutBtn.setOnClickListener {
            val sharedPref = this.getSharedPreferences(
                "playconnectlogintoken",
                Context.MODE_PRIVATE
            )
            val editor = sharedPref.edit()
            editor.putString("userUID", "")
            editor.apply()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        //Set Profile Icon
        val profilePic: ImageView = findViewById(R.id.profilePic)
        when (profilePicture) {
            "2" -> profilePic.setImageResource(R.drawable.woman)
            "else" -> profilePic.setImageResource(R.drawable.man)
        }
    }
}