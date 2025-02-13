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
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreateActivity : AppCompatActivity() {
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

        val nameText: EditText = findViewById(R.id.nameText)
        val descriptionText: EditText = findViewById(R.id.descriptionText)
        val dateText: TextView = findViewById(R.id.dateText)
        val dateBtn: LinearLayout = findViewById(R.id.dateButton)

        val calendar = Calendar.getInstance()
        var timestamp = Timestamp(Date())

        var dateSelected = false
        var sportSelected = false

        val sportBg: TextView = findViewById(R.id.sportBg)
        val padelBtn: LinearLayout = findViewById(R.id.padelButton)
        val padelText: TextView = findViewById(R.id.padelText)
        val padelIcon: ImageView = findViewById(R.id.padelIcon)
        val arrow4: ImageView = findViewById(R.id.arrow4)

        val sportText: TextView = findViewById(R.id.sportText)
        val sportIcon: ImageView = findViewById(R.id.sportIcon)

        val sportBtn: LinearLayout = findViewById(R.id.sportButton)
        sportBtn.setOnClickListener {
            sportBg.visibility = View.VISIBLE
            padelBtn.visibility = View.VISIBLE
            padelText.visibility = View.VISIBLE
            padelIcon.visibility = View.VISIBLE
            arrow4.visibility = View.VISIBLE
        }

        sportBg.setOnClickListener {
            sportBg.visibility = View.GONE
            padelBtn.visibility = View.GONE
            padelText.visibility = View.GONE
            padelIcon.visibility = View.GONE
            arrow4.visibility = View.GONE
        }

        padelBtn.setOnClickListener {
            sportBg.visibility = View.GONE
            padelBtn.visibility = View.GONE
            padelText.visibility = View.GONE
            padelIcon.visibility = View.GONE
            arrow4.visibility = View.GONE
            sportText.text = "Padel"
            sportIcon.setImageResource(R.drawable.padelicon)
            sportSelected = true
        }

        dateBtn.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    dateText.setText(dateFormat.format(calendar.time))

                    TimePickerDialog(
                        this,
                        { _, hourOfDay, minute ->
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            calendar.set(Calendar.MINUTE, minute)
                            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                            dateText.setText("${dateFormat.format(calendar.time)} ${timeFormat.format(calendar.time)}")

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

            val sport = 0
            val place = "Unknown"

            if (nameText.text.isNotEmpty() and descriptionText.text.isNotEmpty() and dateSelected and sportSelected) {
                val match = hashMapOf(
                    "name" to nameText.text.toString(),
                    "description" to descriptionText.text.toString(),
                    "team1" to user,
                    "team2" to "",
                    "sport" to sport,
                    "place" to place,
                    "date" to timestamp
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
                    "You must enter a name, description, and select a date and sport to create a match.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        val checkBtnBg: ImageView = findViewById(R.id.checkBtnBg)
        checkBtnBg.setOnClickListener {
            add()
        }
    }
}