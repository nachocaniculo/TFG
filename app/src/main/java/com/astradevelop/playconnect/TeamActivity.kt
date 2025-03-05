package com.astradevelop.playconnect

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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

        //Get the userID token that will be needed to do the DB queries
        val sharedPref = getSharedPreferences("playconnectlogintoken", Context.MODE_PRIVATE)
        val user = sharedPref.getString("userUID", "")

        val backBtn: ImageView = findViewById(R.id.arrow1)
        backBtn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        val teamInfo = intent.getStringExtra("teamInfo")!!.split(";")

        val teamId = teamInfo[0]
        val teamName = teamInfo[1]
        val sport = teamInfo[2]
        val captain = teamInfo[3]
        val players = teamInfo[4]
        val playerList = players.split(",").toMutableList()

        val sportText: TextView = findViewById(R.id.dateText)
        val nameText: TextView = findViewById(R.id.sportText)
        val sportIcon: ImageView = findViewById(R.id.sportIcon)
        val playersText: TextView = findViewById(R.id.playersTxt)

        val leaveBtn: LinearLayout = findViewById(R.id.eventTV)
        val editText: TextView = findViewById(R.id.editText)

        nameText.text = teamName
        playersText.text = playerList.size.toString()
        val inviteBtn: ImageView = findViewById(R.id.addBtnBg)

        if (user == captain){
            leaveBtn.visibility = View.GONE
            inviteBtn.visibility = View.VISIBLE
            editText.visibility = View.GONE
        }

        val databaseConnection = FirebaseDBConnection()

        leaveBtn.setOnClickListener {
            playerList.remove(user)
            databaseConnection.updatePlayersInTeam(teamId, playerList.joinToString(","))
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        when (sport){
            "0" -> {sportIcon.setImageResource(R.drawable.padelicon)
                sportText.text = "Padel"}
            "1" -> {sportIcon.setImageResource(R.drawable.tennisicon)
                sportText.text = "Tennis"}
            "2" -> {sportIcon.setImageResource(R.drawable.basketicon)
                sportText.text = "Basketball"}
            "3" -> {sportIcon.setImageResource(R.drawable.footballicon)
                sportText.text = "Football"}
        }

        val playerRV: RecyclerView = findViewById(R.id.playerRV)
        inviteBtn.setOnClickListener {
            val message = "Join my team '$teamName' using the following code: $teamId!"
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
            }

            startActivity(Intent.createChooser(intent, "Send invite to:"))
        }

        GlobalScope.launch(Dispatchers.IO) {
            val db = FirebaseFirestore.getInstance()
            val playerNameList = mutableListOf<String>()

            val deferredList = playerList.map { playerId ->
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
                playerRV.layoutManager = LinearLayoutManager(this@TeamActivity)
                playerRV.adapter = PlayersRV(results.toMutableList(), playerList, teamId, user)
            }
        }

    }
}