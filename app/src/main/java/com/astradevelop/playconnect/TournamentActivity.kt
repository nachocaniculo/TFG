package com.astradevelop.playconnect

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class TournamentActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tournament)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backButton: ImageView = findViewById(R.id.arrow1)
        backButton.setOnClickListener {
            finish()
        }
        val sharedPref = getSharedPreferences("playconnectlogintoken", Context.MODE_PRIVATE)
        val user = sharedPref.getString("userUID", "")

        val tournamentNameText: TextView = findViewById(R.id.sportText)
        val sportNameText: TextView = findViewById(R.id.dateText)
        val sportIcon: ImageView = findViewById(R.id.sportIcon)
        val playersNumText: TextView = findViewById(R.id.playersTxt)
        val teamsOrPlayersText: TextView = findViewById(R.id.playersText)

        val db = FirebaseFirestore.getInstance()

        val playersRV: RecyclerView = findViewById(R.id.playerRV)

        val tournamentID = intent.extras!!.getString("tournament")
        val firebaseDBConnection = FirebaseDBConnection()
        val startBtn: LinearLayout = findViewById(R.id.deleteTV)
        val startText: TextView = findViewById(R.id.deleteText)

        GlobalScope.launch {
            val tournament = firebaseDBConnection.findTournamentsByID(tournamentID!!)
            withContext(Dispatchers.Main) {
                tournamentNameText.text = tournament.name
                when (tournament.sport.toString().toInt()){
                    0 -> {
                        sportNameText.text = "Padel"
                        sportIcon.setImageResource(R.drawable.padelicon)
                    }
                    1 -> {
                        sportNameText.text = "Tennis"
                        sportIcon.setImageResource(R.drawable.tennisicon)
                    }
                    2 -> {
                        sportNameText.text = "Basket"
                        sportIcon.setImageResource(R.drawable.basketicon)
                    }
                    3 -> {
                        sportNameText.text = "Football"
                        sportIcon.setImageResource(R.drawable.footballicon)
                    }
                }

                playersNumText.text = tournament.teams.size.toString()
                if (tournament.type.toString().toInt() == 1) {
                    teamsOrPlayersText.text = "Players"
                    GlobalScope.launch(Dispatchers.IO) {
                        val playerNameList = mutableListOf<String>()
                        val playerRatingsList = mutableListOf<String>()

                        val deferredList = tournament.teams.map { playerId ->
                            async {
                                val documentRef = db.collection("players").document(playerId.toString())
                                val document = documentRef.get().await()
                                if (document.exists()) {
                                    val playerName = document.getString("name") ?: "?"
                                    val playerRatings =
                                        document.get("ratings") as? List<Long> ?: emptyList()
                                    val ratingsString = playerRatings.joinToString(",")

                                    playerName to ratingsString
                                } else {
                                    "?" to ""
                                }
                            }
                        }
                        val results = deferredList.awaitAll()

                        results.forEach { (name, ratings) ->
                            playerNameList.add(name)
                            playerRatingsList.add(ratings)
                        }

                        withContext(Dispatchers.Main) {
                            playersRV.layoutManager = LinearLayoutManager(this@TournamentActivity)
                            playersRV.adapter =
                                TournamentPlayersRV(
                                    playerNameList
                                )
                        }
                    }
                }
                else if (tournament.type.toString().toInt() == 2) {
                    teamsOrPlayersText.text = "Teams"
                    GlobalScope.launch(Dispatchers.IO) {
                        val playerNameList = mutableListOf<String>()
                        val playerRatingsList = mutableListOf<String>()

                        val deferredList = tournament.teams.map { playerId ->
                            async {
                                val documentRef =
                                    db.collection("teams").document(playerId.toString())
                                val document = documentRef.get().await()
                                if (document.exists()) {
                                    val playerName = document.getString("name") ?: "?"
                                    val playerRatings =
                                        document.get("ratings") as? List<Long> ?: emptyList()
                                    val ratingsString = playerRatings.joinToString(",")

                                    playerName to ratingsString
                                } else {
                                    "?" to ""
                                }
                            }
                        }
                        val results = deferredList.awaitAll()

                        results.forEach { (name, ratings) ->
                            playerNameList.add(name)
                            playerRatingsList.add(ratings)
                        }

                        withContext(Dispatchers.Main) {
                            playersRV.layoutManager = LinearLayoutManager(this@TournamentActivity)
                            playersRV.adapter =
                                TournamentPlayersRV(
                                    playerNameList
                                )
                        }
                    }
                }
                if (user == tournament.admin){
                    startBtn.visibility = View.VISIBLE
                    startText.visibility = View.VISIBLE
                }
                startBtn.setOnClickListener {
                    val builder = AlertDialog.Builder(this@TournamentActivity)
                    builder.setTitle("Confirm Tournament Start")
                    builder.setMessage("Are you sure you want to start the tournament? Matches will be drawn and new teams won't be able to join.")

                    builder.setPositiveButton("Continue") { _, _ ->
                        CoroutineScope(Dispatchers.IO).launch {
                            firebaseDBConnection.generarTorneoFirestore(tournamentID, tournament.teamMaxNum.toInt())
                        }
                    }

                    builder.setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }

                    val dialog = builder.create()
                    dialog.show()
                }
            }
        }
    }
}