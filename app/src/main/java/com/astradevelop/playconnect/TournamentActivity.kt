package com.astradevelop.playconnect

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
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
    @SuppressLint("SetTextI18n", "CutPasteId")
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

        val noMatchesFoundText: TextView = findViewById(R.id.noMatches)

        fun noMatches(){
            noMatchesFoundText.visibility = View.VISIBLE
            playersRV.visibility = View.GONE
        }
        fun matches(){
            noMatchesFoundText.visibility = View.GONE
            playersRV.visibility = View.VISIBLE
        }

        GlobalScope.launch {
            val tournament = firebaseDBConnection.findTournamentsByID(tournamentID!!)
            db.collection("tournaments")
                .document(tournamentID)
                .collection("matches")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        startBtn.visibility = View.GONE
                        startText.visibility = View.GONE
                    } else {
                        startBtn.visibility = View.VISIBLE
                        startText.visibility = View.VISIBLE
                    }
                }
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

                fun loadTeams() {
                    noMatchesFoundText.visibility = View.GONE
                    playersRV.visibility = View.VISIBLE
                    if (tournament.type.toString().toInt() == 1) {
                        teamsOrPlayersText.text = "Players"
                        lifecycleScope.launch(Dispatchers.IO) {
                            val playerNameList = mutableListOf<String>()
                            val playerRatingsList = mutableListOf<String>()

                            val deferredList = tournament.teams.map { playerId ->
                                async {
                                    val documentRef =
                                        db.collection("players").document(playerId.toString())
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
                                playersRV.layoutManager =
                                    LinearLayoutManager(this@TournamentActivity)
                                playersRV.adapter =
                                    TournamentPlayersRV(
                                        playerNameList
                                    )
                            }
                        }
                    } else if (tournament.type.toString().toInt() == 2) {
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
                                playersRV.layoutManager =
                                    LinearLayoutManager(this@TournamentActivity)
                                playersRV.adapter =
                                    TournamentPlayersRV(
                                        playerNameList
                                    )
                            }
                        }
                    }
                }
                loadTeams()

                fun loadMatches(){
                    lifecycleScope.launch(Dispatchers.IO) {
                        val documentRef = db.collection("tournaments").document(tournamentID).collection("matches")
                        val documents = documentRef.get().await()
                        withContext(Dispatchers.Main) {
                            if (documents.isEmpty) {
                                noMatches()
                            } else {
                                matches()
                                val matches = mutableListOf<TournamentMatch>()
                                for (document in documents) {
                                    val data = document.data
                                    val team1Map = data["team1"] as? Map<String?, String?> ?: emptyMap()
                                    val team2Map = data["team2"] as? Map<String?, String> ?: emptyMap()

                                    val team1 = EquipoSlot(
                                        name = team1Map["name"],
                                        previousMatch = team1Map["previousMatch"]
                                    )

                                    val team2 = EquipoSlot(
                                        name = team2Map["name"],
                                        previousMatch = team2Map["previousMatch"]
                                    )

                                    val match = TournamentMatch(
                                        id = document.id,
                                        team1 = team1,
                                        team2 = team2,
                                        status = data["status"] as? String ?: "Pending",
                                        round = (data["round"] as? Number)?.toLong() ?: 0L
                                    )
                                    matches.add(match)
                                }
                                val sortedMatches = matches.sortedBy { it.round }
                                withContext(Dispatchers.Main) {
                                    playersRV.layoutManager =
                                        LinearLayoutManager(this@TournamentActivity)
                                    playersRV.adapter =
                                        TournamentMatchesRV(
                                            sortedMatches,
                                            tournament.type.toInt(),
                                            tournamentID
                                        )
                                }
                            }
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
                            firebaseDBConnection.generarTorneoFirestore(tournamentID, tournament.teams.size)
                        }
                    }

                    builder.setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }

                    val dialog = builder.create()
                    dialog.show()
                }

                val teamsButton: LinearLayout = findViewById(R.id.playersLL)
                val matchesButton: LinearLayout = findViewById(R.id.matchesLL)
                val teamsText: TextView = findViewById(R.id.playersText)
                val matchesText: TextView = findViewById(R.id.macthesText)

                teamsButton.setOnClickListener {
                    teamsButton.setBackgroundResource(R.drawable.rounded_button)
                    matchesButton.setBackgroundResource(R.drawable.rounded_button_black_nostroke)
                    teamsText.setTextColor(Color.parseColor("#FFFFFF"))
                    matchesText.setTextColor(Color.parseColor("#4F4F4F"))
                    loadTeams()
                }

                matchesButton.setOnClickListener {
                    matchesButton.setBackgroundResource(R.drawable.rounded_button)
                    teamsButton.setBackgroundResource(R.drawable.rounded_button_black_nostroke)
                    matchesText.setTextColor(Color.parseColor("#FFFFFF"))
                    teamsText.setTextColor(Color.parseColor("#4F4F4F"))
                    loadMatches()
                }
            }
        }
    }
}