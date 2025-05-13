package com.astradevelop.playconnect

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ProfileActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "DefaultLocale", "UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPref = getSharedPreferences("playconnectlogintoken", Context.MODE_PRIVATE)
        val userUID = sharedPref.getString("userUID", "")
        val nightMode = sharedPref.getBoolean("nightMode", true)
        val notifications = sharedPref.getBoolean("notifications", true)

        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        //Display user info
        val email = intent.extras!!.getString("email")
        var user = intent.extras!!.getString("user")
        var username = intent.extras!!.getString("username")
        val profilePicture = intent.extras!!.getString("profilePicture")
        val ratings = intent.getStringExtra("ratings")
            ?.split(",")
            ?.mapNotNull { it.toIntOrNull() }
            ?: emptyList()

        var ratingTotal = 0

        for (ratingTemp in ratings) {
            ratingTotal += ratingTemp
        }

        val ratingMean = String.format("%.1f", ratingTotal.toDouble() / ratings.size).toDouble()

        val star1: ImageView = findViewById(R.id.star1)
        val star2: ImageView = findViewById(R.id.star2)
        val star3: ImageView = findViewById(R.id.star3)
        val star4: ImageView = findViewById(R.id.star4)
        val star5: ImageView = findViewById(R.id.star5)

        fun printStars(){
            if (ratingMean > 0){
                if (ratingMean >= 1) {
                    star1.setImageResource(R.drawable.star_full)
                } else {
                    star1.setImageResource(R.drawable.star_half)
                }
            }
            if (ratingMean > 1){
                if (ratingMean >= 2) {
                    star2.setImageResource(R.drawable.star_full)
                } else {
                    star2.setImageResource(R.drawable.star_half)
                }
            }
            if (ratingMean > 2){
                if (ratingMean >= 3) {
                    star3.setImageResource(R.drawable.star_full)
                } else {
                    star3.setImageResource(R.drawable.star_half)
                }
            }
            if (ratingMean > 3){
                if (ratingMean >= 4) {
                    star4.setImageResource(R.drawable.star_full)
                } else {
                    star4.setImageResource(R.drawable.star_half)
                }
            }
            if (ratingMean > 4){
                if (ratingMean >= 5) {
                    star5.setImageResource(R.drawable.star_full)
                } else {
                    star5.setImageResource(R.drawable.star_half)
                }
            }
        }

        printStars()

        val ratingTV: TextView = findViewById(R.id.ratingText)
        if (ratingMean.toString() == "NaN"){
            ratingTV.text = "-"
        } else {
            ratingTV.text = ratingMean.toString()
        }

        val userTxt: TextView = findViewById(R.id.userTxt)
        userTxt.text = user

        val mailTxt: TextView = findViewById(R.id.mailText)
        mailTxt.text = email

        //Back button
        val backBtn: ImageView = findViewById(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
        }

        val editor = sharedPref.edit()

        val notificationSwitch: Switch = findViewById(R.id.switch1)
        notificationSwitch.isChecked = notifications
        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                editor.putBoolean("notifications", true)
                editor.apply()
            } else {
                editor.putBoolean("notifications", false)
                editor.apply()
            }
        }

        val switchNightMode: Switch = findViewById(R.id.switch2)

        if (nightMode) {
            switchNightMode.isChecked = true
        } else {
            switchNightMode.isChecked = false
        }

        switchNightMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                editor.putBoolean("nightMode", true)
                editor.apply()
            } else {
                editor.putBoolean("nightMode", false)
                editor.apply()
            }
            recreate()
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

        profilePic.setOnClickListener {
            val intent = Intent(this, ProfilePictureActivity::class.java)
            startActivity(intent)
        }

        val editProfileBtn: LinearLayout = findViewById(R.id.signUpBtn)
        editProfileBtn.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            intent.putExtra("user", user)
            intent.putExtra("email", email)
            intent.putExtra("username", username)
            intent.putExtra("profilePicture", profilePicture)
            intent.putExtra("ratings", ratings.joinToString (","))
            startActivity(intent)
            finish()
        }
    }
}