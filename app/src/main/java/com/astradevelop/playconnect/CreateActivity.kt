package com.astradevelop.playconnect

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import java.util.Date
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.Constraint
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreateActivity : AppCompatActivity() {

    lateinit var sportBg: TextView
    lateinit var sportRV: RecyclerView

    lateinit var sportText: TextView
    lateinit var sportIcon: ImageView

    private var sportSelected = false
    private var sport = 0
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backBtn: ImageView = findViewById(R.id.arrow1)
        backBtn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        var type = 0

        val nameText: EditText = findViewById(R.id.nameText)
        val descriptionText: EditText = findViewById(R.id.descriptionText)
        val dateText: TextView = findViewById(R.id.dateText)
        val dateBtn: LinearLayout = findViewById(R.id.dateButton)

        val eventCL: ConstraintLayout = findViewById(R.id.eventCL)
        val teamCL: ConstraintLayout = findViewById(R.id.teamCL)

        val maxPlayer: EditText = findViewById(R.id.maxPlayers)

        val calendar = Calendar.getInstance()
        var timestamp = Timestamp(Date())

        var dateSelected = false

        sportBg = findViewById(R.id.sportBg)
        sportRV = findViewById(R.id.sportsRV)

        sportText = findViewById(R.id.sportText)
        sportIcon = findViewById(R.id.sportIcon)

        val teamBtn: LinearLayout = findViewById(R.id.teamTV)
        val eventBtn: LinearLayout = findViewById(R.id.eventTV)

        val teamTxt: TextView = findViewById(R.id.teamText)
        val eventTxt: TextView = findViewById(R.id.scheduledText)

        teamBtn.setOnClickListener {
            teamBtn.setBackgroundResource(R.drawable.rounded_button)
            eventBtn.setBackgroundResource(R.drawable.rounded_button_black_nostroke)
            eventCL.visibility = View.GONE
            teamCL.visibility = View.VISIBLE
            teamTxt.setTextColor(Color.parseColor("#FFFFFF"))
            eventTxt.setTextColor(Color.parseColor("#4F4F4F"))
            type = 2
        }

        eventBtn.setOnClickListener {
            teamBtn.setBackgroundResource(R.drawable.rounded_button_black_nostroke)
            eventBtn.setBackgroundResource(R.drawable.rounded_button)
            eventCL.visibility = View.VISIBLE
            teamCL.visibility = View.GONE
            eventTxt.setTextColor(Color.parseColor("#FFFFFF"))
            teamTxt.setTextColor(Color.parseColor("#4F4F4F"))
            type = 0
        }

        val sportBtn: LinearLayout = findViewById(R.id.sportButton)
        sportBtn.setOnClickListener {
            sportBg.visibility = View.VISIBLE
            sportRV.visibility = View.VISIBLE
            sportRV.layoutManager = LinearLayoutManager(this)
            sportRV.adapter = SportsRV(this)
        }

        sportBg.setOnClickListener {
            sportBg.visibility = View.GONE
            sportRV.visibility = View.GONE
        }

        dateBtn.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    dateText.text = dateFormat.format(calendar.time)

                    TimePickerDialog(
                        this,
                        { _, hourOfDay, minute ->
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            calendar.set(Calendar.MINUTE, minute)
                            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                            dateText.text = "${dateFormat.format(calendar.time)} ${timeFormat.format(calendar.time)}"

                            timestamp = Timestamp(calendar.time)
                            dateSelected = true
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    ).show()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        fun add() {
            val sharedPref = getSharedPreferences("playconnectlogintoken", Context.MODE_PRIVATE)
            val user = sharedPref.getString("userUID", "")

            val place = "Unknown"

            if (nameText.text.isNotEmpty() and descriptionText.text.isNotEmpty()  and maxPlayer.text.isNotEmpty() and dateSelected and sportSelected) {
                val match = hashMapOf(
                    "name" to nameText.text.toString(),
                    "description" to descriptionText.text.toString(),
                    "team1" to user,
                    "team2" to "",
                    "sport" to sport,
                    "place" to place,
                    "date" to timestamp,
                    "maxPlayers" to maxPlayer.text.toString()
                )

                val db = FirebaseFirestore.getInstance()
                db.collection("match").document()
                    .set(match)
                    .addOnCompleteListener { dbTask ->
                        if (dbTask.isSuccessful) {
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                this,
                                "Error saving user data: ${dbTask.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error saving user data: $e", Toast.LENGTH_SHORT)
                            .show()
                    }
            } else {
                Toast.makeText(
                    this,
                    "You must enter a name, description, maximum players, and select a date and sport to create a match.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        fun addTeam() {
            val sharedPref = getSharedPreferences("playconnectlogintoken", Context.MODE_PRIVATE)
            val user = sharedPref.getString("userUID", "")


            if (nameText.text.isNotEmpty() and maxPlayer.text.isNotEmpty() and sportSelected) {
                val team = hashMapOf(
                    "name" to nameText.text.toString(),
                    "captain" to user,
                    "sport" to sport,
                    "maxPlayers" to maxPlayer.text.toString(),
                    "players" to mutableListOf(user)
                )

                val db = FirebaseFirestore.getInstance()
                db.collection("teams").document()
                    .set(team)
                    .addOnCompleteListener { dbTask ->
                        if (dbTask.isSuccessful) {
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                this,
                                "Error saving user data: ${dbTask.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error saving user data: $e", Toast.LENGTH_SHORT)
                            .show()
                    }
            } else {
                Toast.makeText(
                    this,
                    "You must enter a name, description, maximum players, and select a date and sport to create a match.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        val checkBtnBg: ImageView = findViewById(R.id.checkBtnBg)
        checkBtnBg.setOnClickListener {
            when (type) {
                0 -> add()
                2 -> addTeam()
            }
        }
    }

    fun setSport(sportTemp: Int){
        when (sportTemp){
            0 -> {sportText.text = "Padel"
                sportIcon.setImageResource(R.drawable.padelicon)}
            1 -> {sportText.text = "Tennis"
                sportIcon.setImageResource(R.drawable.tennisicon)}
            2 -> {sportText.text = "Basketball"
                sportIcon.setImageResource(R.drawable.basketicon)}
            3 -> {sportText.text = "Football"
                sportIcon.setImageResource(R.drawable.footballicon)}
        }
        sportBg.visibility = View.GONE
        sportRV.visibility = View.GONE
        sportSelected = true
        sport = sportTemp
    }
}