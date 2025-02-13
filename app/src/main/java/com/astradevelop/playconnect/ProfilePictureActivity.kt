package com.astradevelop.playconnect

import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ProfilePictureActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile_picture)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Get the userID token that will be needed to do the DB queries
        val sharedPref = getSharedPreferences("playconnectlogintoken", Context.MODE_PRIVATE)
        val user = sharedPref.getString("userUID", "")

        //Image Buttons
        val manImage: ImageView = findViewById(R.id.profilePic2)
        val womanImage: ImageView = findViewById(R.id.profilePic3)

        //Instance from FirebaseDBConnection
        val dbConnection = FirebaseDBConnection()

        manImage.setOnClickListener {
            dbConnection.updateDocumentPicture(this, user!!, "1")
        }

        womanImage.setOnClickListener {
            dbConnection.updateDocumentPicture(this, user!!, "2")
        }
    }
}