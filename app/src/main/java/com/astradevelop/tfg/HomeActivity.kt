package com.astradevelop.tfg

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.Image
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Get the userID token that will be needed to do the DB queries
        val sharedPref = getSharedPreferences("playconnectlogintoken", Context.MODE_PRIVATE)
        val user = sharedPref.getString("userUID", "")

        //TextView for the greeting
        val greetingTxt: TextView = findViewById(R.id.homeText2)

        //Firebase DB connection for the queries and default values to store them
        val db = FirebaseFirestore.getInstance()
        var username = ""
        var mail = ""
        var name = ""
        var profilePicture = ""

        //Settings menu button handler
        val profilePic : ImageView = findViewById(R.id.profilePic)
        profilePic.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("user", name)
            intent.putExtra("email", mail)
            intent.putExtra("profilePicture", profilePicture)
            startActivity(intent)
        }

        //Query to get the user email, username, and name
        db.collection("players").document(user!!)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    username = document.getString("username").toString()
                    mail = document.getString("email").toString()
                    name = document.getString("name").toString()
                    profilePicture = document.getString("picture").toString()
                    greetingTxt.text = "Hello,\n$username"

                    //Set profile Picture
                    when (profilePicture) {
                        "2" -> profilePic.setImageResource(R.drawable.woman)
                        "else" -> profilePic.setImageResource(R.drawable.man)
                    }
                } else {
                    println("No existe el documento")
                }
            }
            .addOnFailureListener { exception ->
                println("Error al obtener el documento: $exception")
            }


        //Scheduled Matches and Tournaments handlers
        val matchesBtn: LinearLayout = findViewById(R.id.scheduledTV)
        val tournamentsBtn: LinearLayout = findViewById(R.id.tournamentsTV)

        val matchesTxt: TextView = findViewById(R.id.scheduledText)
        val tournamentsTxt: TextView = findViewById(R.id.tournamentsText)

        matchesBtn.setOnClickListener {
            matchesBtn.setBackgroundResource(R.drawable.rounded_button)
            tournamentsBtn.setBackgroundResource(R.drawable.rounded_button_black_nostroke)
            matchesTxt.setTextColor(Color.parseColor("#FFFFFF"))
            tournamentsTxt.setTextColor(Color.parseColor("#4F4F4F"))
        }

        tournamentsBtn.setOnClickListener {
            tournamentsBtn.setBackgroundResource(R.drawable.rounded_button)
            matchesBtn.setBackgroundResource(R.drawable.rounded_button_black_nostroke)
            tournamentsTxt.setTextColor(Color.parseColor("#FFFFFF"))
            matchesTxt.setTextColor(Color.parseColor("#4F4F4F"))
        }

    }
}