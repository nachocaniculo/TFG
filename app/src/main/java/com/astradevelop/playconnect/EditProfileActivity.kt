package com.astradevelop.playconnect

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class EditProfileActivity : AppCompatActivity() {

    private var email = ""
    private var user = ""
    private var username = ""
    private var profilePicture = ""
    private var ratings: List<Int> = emptyList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val sharedPref = getSharedPreferences("playconnectlogintoken", Context.MODE_PRIVATE)
        val userUID = sharedPref.getString("userUID", "")

        email = intent.extras!!.getString("email").toString()
        user = intent.extras!!.getString("user").toString()
        username = intent.extras!!.getString("username").toString()
        profilePicture = intent.extras!!.getString("profilePicture").toString()
        ratings = intent.getStringExtra("ratings")
            ?.split(",")
            ?.mapNotNull { it.toIntOrNull() }
            ?: emptyList()

        val backBtn: ImageView = findViewById(R.id.backBtn)
        backBtn.setOnClickListener { onBackPressed() }

        val emailET: EditText = findViewById(R.id.emailET)
        emailET.setText(email)

        val nameET: EditText = findViewById(R.id.nameET)
        nameET.setText(user)

        val usernameET: EditText = findViewById(R.id.usernameET)
        usernameET.setText(username)

        val dbConnection = FirebaseDBConnection()

        val saveBtn: LinearLayout = findViewById(R.id.signUpBtn)
        saveBtn.setOnClickListener {
            user = nameET.text.toString()
            username = usernameET.text.toString()
            email = emailET.text.toString()
            dbConnection.updatePlayerInfo(
                Player(
                    userUID!!,
                    user,
                    username,
                    email,
                    profilePicture,
                    ratings
                )
            )
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra("user", user)
        intent.putExtra("email", email)
        intent.putExtra("username", username)
        intent.putExtra("profilePicture", profilePicture)
        intent.putExtra("ratings", ratings.joinToString {","})
        startActivity(intent)
        finish()
    }
}