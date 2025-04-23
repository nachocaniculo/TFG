package com.astradevelop.playconnect

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
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

    private lateinit var matchData: Match
    private var matchID = ""
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_match)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        matchID = intent.extras!!.getString("MatchID")!!

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

        val matchTypeText: TextView = findViewById(R.id.playersText)

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

            if (match.type == 1.toLong()) {
                GlobalScope.launch(Dispatchers.IO) {
                    val playerNameList = mutableListOf<String>()
                    val playerRatingsList = mutableListOf<String>()

                    val deferredList = players.map { playerId ->
                        async {
                            val documentRef = db.collection("players").document(playerId)
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
                        playersRV.layoutManager = LinearLayoutManager(this@MatchActivity)
                        playersRV.adapter =
                            MatchPlayersRV(playerNameList, playerRatingsList, players, matchID!!)
                    }
                }
            } else {
                matchTypeText.text = "Teams"
                GlobalScope.launch(Dispatchers.IO) {
                    val playerNameList = mutableListOf<String>()
                    val playerRatingsList = mutableListOf<String>()

                    val deferredList = players.map { playerId ->
                        async {
                            val documentRef = db.collection("teams").document(playerId)
                            val document = documentRef.get().await()
                            if (document.exists()) {
                                val playerName = document.getString("name") ?: "?"
                                val ratingsString = "6"

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
                        playersRV.layoutManager = LinearLayoutManager(this@MatchActivity)
                        playersRV.adapter =
                            MatchPlayersRV(playerNameList, playerRatingsList, players, matchID!!)
                    }
                }
            }

        }

        GlobalScope.launch {
            val matchDataTemp = FirebaseDBConnection().findMatchById(matchID!!)
            withContext(Dispatchers.Main) {
                if (matchDataTemp != null) {
                    updateMatch(matchDataTemp)
                    matchData = matchDataTemp
                }
            }
        }

        val teamsText: TextView = findViewById(R.id.teamsText)
        val teamsRV: RecyclerView = findViewById(R.id.teamsRV)

        val addBg: TextView = findViewById(R.id.addBg)
        addBg.setOnClickListener {
            addBg.visibility = View.GONE
            teamsText.visibility = View.GONE
            teamsRV.visibility = View.GONE
        }

        joinButton.setOnClickListener {
            if (matchData.type == 1.toLong()) {
                if (maxPlayers > players.size) {
                    FirebaseDBConnection().updateTeam(matchID, userUID!!, this)
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
            } else {
                addBg.visibility = View.VISIBLE
                teamsText.visibility = View.VISIBLE
                teamsRV.visibility = View.VISIBLE
                GlobalScope.launch {
                    val databaseConnection = FirebaseDBConnection()
                    val teamList = databaseConnection.findTeamsByPlayerAndSport(userUID!!, matchData.sport)
                    withContext(Dispatchers.Main) {
                        if (teamList.isEmpty()) {
                            addBg.visibility = View.GONE
                            teamsText.visibility = View.GONE
                            teamsRV.visibility = View.GONE
                            Toast.makeText(
                                this@MatchActivity,
                                "No teams found for this sport",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            teamsRV.layoutManager = LinearLayoutManager(this@MatchActivity)
                            teamsRV.adapter = TeamsSearchRV(teamList, this@MatchActivity)
                        }
                    }
                }
            }
        }
    }

    fun chooseTeam(id:String){
        FirebaseDBConnection().updateTeam(matchID, id, this)
        Toast.makeText(
            this,
            "Joined!",
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }
}