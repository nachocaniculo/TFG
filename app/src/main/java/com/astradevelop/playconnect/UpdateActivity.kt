package com.astradevelop.playconnect

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
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
import android.text.Editable
import android.widget.EditText
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UpdateActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update)
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

        val db = FirebaseFirestore.getInstance()

        val matchId = intent.getStringExtra("id")!!
        val date = intent.getLongExtra("date", -1L)
        val location = intent.getStringExtra("location")

        val locationText: EditText = findViewById(R.id.location)
        location?.let {
            locationText.text = Editable.Factory.getInstance().newEditable(it)
        }

        val dateBtn: LinearLayout = findViewById(R.id.dateButton)

        val dateText: TextView = findViewById(R.id.dateText)
        if (date != -1L) {
            val date = Date(date * 1000)
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            dateText.text = format.format(date)
        } else {
            dateText.text = "Wrong date"
        }

        val calendar = Calendar.getInstance()
        var timestamp = Timestamp(Date())
        var dateSelected = false

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

        fun deleteMatch(){
            db.collection("match").document(matchId)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "Match successfully deleted",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Error deleting match: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

        fun showDeleteConfirmationDialog() {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirm Deletion")
            builder.setMessage("Are you sure you want to delete this match?")

            builder.setPositiveButton("Delete") { _, _ ->
                deleteMatch()
            }

            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

        val deleteBtn: ImageView = findViewById(R.id.deleteBtnBg)
        deleteBtn.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        val firebaseDBConnection = FirebaseDBConnection()

        val saveButton: ImageView = findViewById(R.id.checkButtonOverlay)
        saveButton.setOnClickListener {
            firebaseDBConnection.updateDateAndLocation(matchId, timestamp, locationText.text.toString())
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}