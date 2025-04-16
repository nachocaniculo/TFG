package com.astradevelop.playconnect

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class TeamActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_team)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPref = getSharedPreferences("playconnectlogintoken", Context.MODE_PRIVATE)
        val user = sharedPref.getString("userUID", "")

        val backBtn: ImageView = findViewById(R.id.arrow1)
        backBtn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        val teamId = intent.getStringExtra("teamInfo")

        val databaseConnection = FirebaseDBConnection()
        val inviteBtn: ImageView = findViewById(R.id.addBtnBg)

        var teamName = ""
        var sport: String
        var captain: String
        var players: MutableList<String>

        val playerRV: RecyclerView = findViewById(R.id.playerRV)
        inviteBtn.setOnClickListener {
            val message = "Join my team '$teamName' using the following code: $teamId!"
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
            }

            startActivity(Intent.createChooser(intent, "Send invite to:"))
        }

        val db = FirebaseFirestore.getInstance()

        val deleteBtn: LinearLayout = findViewById(R.id.deleteTV)
        val deleteText: TextView = findViewById(R.id.deleteText)

        deleteBtn.setOnClickListener{
            db.collection("teams").document(teamId!!)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "Team successfully deleted",
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

        val noMatches: TextView = findViewById(R.id.noMatches)

        fun loadPlayers() {
            lifecycleScope.launch {
                noMatches.visibility = View.GONE
                playerRV.visibility = View.VISIBLE
                val team = databaseConnection.findTeamById(teamId!!)!!

                teamName = team.name
                sport = team.sport.toString()
                captain = team.captain
                players = (team.players as? List<String>)?.toMutableList() ?: mutableListOf()

                val sportText: TextView = findViewById(R.id.dateText)
                val nameText: TextView = findViewById(R.id.sportText)
                val sportIcon: ImageView = findViewById(R.id.sportIcon)
                val playersText: TextView = findViewById(R.id.playersTxt)

                val leaveBtn: LinearLayout = findViewById(R.id.eventTV)
                val editText: TextView = findViewById(R.id.editText)

                nameText.text = teamName
                playersText.text = players.size.toString()

                if (user == captain) {
                    leaveBtn.visibility = View.GONE
                    inviteBtn.visibility = View.VISIBLE
                    deleteBtn.visibility = View.VISIBLE
                    deleteText.visibility = View.VISIBLE
                    editText.visibility = View.GONE
                }

                leaveBtn.setOnClickListener {
                    players.remove(user)
                    databaseConnection.updatePlayersInTeam(teamId, players)
                    val intent = Intent(this@TeamActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                when (sport) {
                    "0" -> {
                        sportIcon.setImageResource(R.drawable.padelicon)
                        sportText.text = "Padel"
                    }

                    "1" -> {
                        sportIcon.setImageResource(R.drawable.tennisicon)
                        sportText.text = "Tennis"
                    }

                    "2" -> {
                        sportIcon.setImageResource(R.drawable.basketicon)
                        sportText.text = "Basketball"
                    }

                    "3" -> {
                        sportIcon.setImageResource(R.drawable.footballicon)
                        sportText.text = "Football"
                    }
                }

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
                        playerRV.layoutManager = LinearLayoutManager(this@TeamActivity)
                        playerRV.adapter = TeamPlayersRV(
                            playerNameList.toMutableList(),
                            playerRatingsList,
                            players,
                            teamId!!,
                            user,
                            captain
                        )
                    }
                }
            }
        }
        loadPlayers()

        fun loadMatches() {
            lifecycleScope.launch {
                val matchData = databaseConnection.findMatchesByTeam(teamId!!)
                if (matchData.isNotEmpty()) {
                    playerRV.layoutManager = LinearLayoutManager(this@TeamActivity)
                    playerRV.adapter = TeamMatchesRV(matchData)
                    noMatches.visibility = View.GONE
                    playerRV.visibility = View.VISIBLE
                } else {
                    playerRV.visibility = View.GONE
                    noMatches.visibility = View.VISIBLE
                }
            }
        }

        fun loadPastMatches() {
            lifecycleScope.launch {
                val matchData = databaseConnection.findPastMatchesByTeam(teamId!!)
                if (matchData.isNotEmpty()) {
                    playerRV.layoutManager = LinearLayoutManager(this@TeamActivity)
                    playerRV.adapter = TeamMatchesRV(matchData)
                    noMatches.visibility = View.GONE
                    playerRV.visibility = View.VISIBLE
                } else {
                    playerRV.visibility = View.GONE
                    noMatches.visibility = View.VISIBLE
                }
            }
        }

        val playersButton: LinearLayout = findViewById(R.id.playersLL)
        val playersText: TextView = findViewById(R.id.playersText)

        val matchesButton: LinearLayout = findViewById(R.id.matchesLL)
        val matchesText: TextView = findViewById(R.id.macthesText)

        val pastMatchesButton: LinearLayout = findViewById(R.id.pastMatchesLL)
        val pastMatchesText: TextView = findViewById(R.id.pastMacthesText)

        playersButton.setOnClickListener {
            playersButton.setBackgroundResource(R.drawable.rounded_button)
            matchesButton.setBackgroundResource(R.drawable.rounded_button_black_nostroke)
            pastMatchesButton.setBackgroundResource(R.drawable.rounded_button_black_nostroke)
            playersText.setTextColor(Color.parseColor("#FFFFFF"))
            matchesText.setTextColor(Color.parseColor("#4F4F4F"))
            pastMatchesText.setTextColor(Color.parseColor("#4F4F4F"))
            loadPlayers()
        }

        matchesButton.setOnClickListener {
            matchesButton.setBackgroundResource(R.drawable.rounded_button)
            playersButton.setBackgroundResource(R.drawable.rounded_button_black_nostroke)
            pastMatchesButton.setBackgroundResource(R.drawable.rounded_button_black_nostroke)
            matchesText.setTextColor(Color.parseColor("#FFFFFF"))
            playersText.setTextColor(Color.parseColor("#4F4F4F"))
            pastMatchesText.setTextColor(Color.parseColor("#4F4F4F"))
            loadMatches()
        }

        pastMatchesButton.setOnClickListener {
            pastMatchesButton.setBackgroundResource(R.drawable.rounded_button)
            playersButton.setBackgroundResource(R.drawable.rounded_button_black_nostroke)
            matchesButton.setBackgroundResource(R.drawable.rounded_button_black_nostroke)
            pastMatchesText.setTextColor(Color.parseColor("#FFFFFF"))
            playersText.setTextColor(Color.parseColor("#4F4F4F"))
            matchesText.setTextColor(Color.parseColor("#4F4F4F"))
            loadPastMatches()
        }

    }
}