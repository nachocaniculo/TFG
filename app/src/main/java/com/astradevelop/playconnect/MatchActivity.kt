package com.astradevelop.playconnect

import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class MatchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_match)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val matchID = intent.extras!!.getString("MatchID")

        val sharedPref = getSharedPreferences("playconnectlogintoken", Context.MODE_PRIVATE)
        val userUID = sharedPref.getString("userUID", "")

        val backButton: ImageView = findViewById(R.id.arrow1)
        backButton.setOnClickListener {
            finish()
        }

        val matchName: TextView = findViewById(R.id.sportText)
        val dateText: TextView = findViewById(R.id.dateText)
        val locationText: TextView = findViewById(R.id.locationText)
        val sportIcon: ImageView = findViewById(R.id.sportIcon)

        val joinButton: LinearLayout = findViewById(R.id.joinButton)

        var maxPlayers = 0
        var players: MutableList<String> = mutableListOf()

        fun updateMatch(match: Match){
            matchName.text = match.name
            locationText.text = match.place
            maxPlayers = match.maxPlayers.toInt()

            val timestamp: Timestamp = match.date

            val date = timestamp.toDate()

            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val formattedDate = dateFormat.format(date)

            dateText.text = formattedDate

            when (match.sport.toString()){
                "0" -> sportIcon.setImageResource(R.drawable.padelicon)
                "1" -> sportIcon.setImageResource(R.drawable.tennisicon)
                "2" -> sportIcon.setImageResource(R.drawable.basketicon)
                "3" -> sportIcon.setImageResource(R.drawable.footballicon)
            }

            players = (match.players as? List<String>)?.toMutableList() ?: mutableListOf()

            val db = FirebaseFirestore.getInstance()

            val playersRV: RecyclerView = findViewById(R.id.playerRV)

            GlobalScope.launch(Dispatchers.IO) {
                val playerNameList = mutableListOf<String>()

                val deferredList = players.map { playerId ->
                    async {
                        val documentRef = db.collection("players").document(playerId)
                        val document = documentRef.get().await() // Esperar la respuesta
                        if (document.exists()) {
                            document.getString("name") ?: "?"
                        } else {
                            "?"
                        }
                    }
                }
                val results = deferredList.awaitAll()

                playerNameList.addAll(results)

                withContext(Dispatchers.Main) {
                    playersRV.layoutManager = LinearLayoutManager(this@MatchActivity)
                    playersRV.adapter = MatchPlayersRV(results.toMutableList(), players, matchID!!)
                }
            }
        }

        GlobalScope.launch {
            val matchData = FirebaseDBConnection().findMatchById(matchID!!)
            withContext(Dispatchers.Main) {
                if (matchData != null) {
                    updateMatch(matchData)
                }
            }
        }

        joinButton.setOnClickListener {
            if (maxPlayers > players.size) {
                FirebaseDBConnection().updateTeam(matchID!!, userUID!!)
                Toast.makeText(
                    this,
                    "Joined!",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } else {
                Toast.makeText(
                    this,
                    "Match is already full",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}