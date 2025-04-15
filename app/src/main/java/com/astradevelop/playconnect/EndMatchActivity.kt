package com.astradevelop.playconnect

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class EndMatchActivity : AppCompatActivity() {

    var listOfRatings: MutableList<Int> = mutableListOf()
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_end_match)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var players = intent.getStringExtra("players")!!.split(",") as MutableList<String>
        val matchID = intent.getStringExtra("match")!!

        val playersRV: RecyclerView = findViewById(R.id.playersRV)

        val sharedPref = getSharedPreferences("playconnectlogintoken", Context.MODE_PRIVATE)
        val user = sharedPref.getString("userUID", "")

        val firebaseDBConnection = FirebaseDBConnection()

        if (players.isEmpty()){
            Toast.makeText(
                this,
                "Error retrieving players. Try again",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        } else {
            if (players.size>1) {
                players.remove(user)
                playersRV.layoutManager = LinearLayoutManager(this)
                playersRV.adapter = RatePlayersRV(players, this)
            } else {
                Toast.makeText(
                    this,
                    "You are the only player for this match. Ending it...",
                    Toast.LENGTH_SHORT
                ).show()
                firebaseDBConnection.endMatch(matchID, user!!)
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        fun save(){
            for (player in players){
                if (listOfRatings[players.indexOf(player)]>0) {
                    firebaseDBConnection.ratePlayer(player, listOfRatings[players.indexOf(player)])
                }
            }
            firebaseDBConnection.endMatch(matchID, user!!)
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        val saveButton: LinearLayout = findViewById(R.id.playersLL)
        saveButton.setOnClickListener {
            var canSave = true
            for (rating in listOfRatings){
                if (rating == 0){
                    canSave = false
                }
            }
            if (canSave){
                save()
            } else {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Confirm Deletion")
                builder.setMessage("You still have players to rate. Are you sure you want to continue?")

                builder.setPositiveButton("Continue") { _, _ ->
                    save()
                }

                builder.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }

                val dialog = builder.create()
                dialog.show()
            }
        }
    }

    fun updateList(value: Int, position: Int){
        if (value == 0){
            listOfRatings.add(value)
        } else {
            listOfRatings[position] = value
        }
    }
}