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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Get the userID token that will be needed to do the DB queries
        val sharedPref = getSharedPreferences("playconnectlogintoken", Context.MODE_PRIVATE)
        val user = sharedPref.getString("userUID", "")

        //TextView for the greeting
        val greetingTxt: TextView = findViewById(R.id.homeText2)

        //Firebase DB connection for the queries and default values to store them
        val db = FirebaseFirestore.getInstance()
        var username = ""
        var mail = ""
        var name = ""
        var profilePicture = ""
        var ratings: List<Int> = emptyList()

        val profilePic : ImageView = findViewById(R.id.profilePic)
        profilePic.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("user", name)
            intent.putExtra("email", mail)
            intent.putExtra("username", username)
            intent.putExtra("profilePicture", profilePicture)
            intent.putExtra("ratings", ratings.joinToString(","))
            startActivity(intent)
        }
        db.collection("players").document(user!!)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    username = document.getString("username").toString()
                    mail = document.getString("email").toString()
                    name = document.getString("name").toString()
                    profilePicture = document.getString("picture").toString()
                    ratings = (document.get("ratings") as? List<Long>)?.map { it.toInt() } ?: emptyList()
                    greetingTxt.text = "Hello,\n$username"

                    //Set profile Picture
                    when (profilePicture) {
                        "2" -> profilePic.setImageResource(R.drawable.woman)
                        "else" -> profilePic.setImageResource(R.drawable.man)
                    }
                } else {
                    println("No existe el documento")
                }
            }
            .addOnFailureListener { exception ->
                println("Error al obtener el documento: $exception")
            }

        val matchesBtn: LinearLayout = findViewById(R.id.eventTV)
        val tournamentsBtn: LinearLayout = findViewById(R.id.tournamentsTV)
        val pastBtn: LinearLayout = findViewById(R.id.pastTV)

        val matchesTxt: TextView = findViewById(R.id.scheduledText)
        val tournamentsTxt: TextView = findViewById(R.id.tournamentsText)
        val pastTxt: TextView = findViewById(R.id.pastText)

        val addBg: TextView = findViewById(R.id.addBg)
        val searchTV: LinearLayout = findViewById(R.id.searchTV)
        val searchText: TextView = findViewById(R.id.searchText)
        val createTV: LinearLayout = findViewById(R.id.createTV)
        val createText: TextView = findViewById(R.id.createText)
        val addBtnOverlay: ImageView = findViewById(R.id.addButtonOverlay)

        var menu = false

        val addBtnBg: ImageView = findViewById(R.id.addBtnBg)
        addBtnBg.setOnClickListener {
            if (menu){
                addBg.visibility = View.GONE
                searchTV.visibility = View.GONE
                searchText.visibility = View.GONE
                createTV.visibility = View.GONE
                createText.visibility = View.GONE
                addBtnOverlay.setImageResource(android.R.drawable.ic_input_add)
            } else {
                addBg.visibility = View.VISIBLE
                searchTV.visibility = View.VISIBLE
                searchText.visibility = View.VISIBLE
                createTV.visibility = View.VISIBLE
                createText.visibility = View.VISIBLE
                addBtnOverlay.setImageResource(R.drawable.back)
            }
            menu = !menu
        }

        searchTV.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        createTV.setOnClickListener {
            val intent = Intent(this, CreateActivity::class.java)
            startActivity(intent)
        }

        val matchesRV: RecyclerView = findViewById(R.id.matchesRV)
        val noResultsText: TextView = findViewById(R.id.noResultsText)

        fun noMatches(){
            noResultsText.visibility = View.VISIBLE
            matchesRV.visibility = View.GONE
        }

        fun matches(matchList: ArrayList<Match>) {
            noResultsText.visibility = View.GONE
            matchesRV.visibility = View.VISIBLE
            matchesRV.layoutManager = LinearLayoutManager(this)
            matchesRV.adapter = HomeMatchesRV(matchList, this, user)
        }

        val teamsRV1: RecyclerView = findViewById(R.id.teamsRV1)
        val teamsRV2: RecyclerView = findViewById(R.id.teamsRV2)

        fun noTeams(){
            teamsRV1.visibility = View.GONE
            teamsRV2.visibility = View.GONE
        }

        fun teams(teamList: ArrayList<Team>) {
            teamsRV1.visibility = View.VISIBLE
            teamsRV2.visibility = View.VISIBLE
            teamsRV1.layoutManager = LinearLayoutManager(this)
            teamsRV2.layoutManager = LinearLayoutManager(this)
            val middle = teamList.size / 2
            val firstHalf = teamList.subList(0, middle)
            val secondHalf = teamList.subList(middle, teamList.size)
            teamsRV1.adapter = HomeTeamsRV(secondHalf, this, user)
            teamsRV2.adapter = HomeTeamsRV2(firstHalf, this, user)
        }

        fun loadMatches() {
            val databaseConnection = FirebaseDBConnection()
            lifecycleScope.launch {
                val matchList = databaseConnection.findMatches(user)
                if (matchList.isEmpty()) {
                    noMatches()
                } else {
                    matches(matchList)
                }
            }
        }
        loadMatches()

        fun loadPastMatches() {
            val databaseConnection = FirebaseDBConnection()
            lifecycleScope.launch {
                val matchList = databaseConnection.findPastMatches(user)
                if (matchList.isEmpty()) {
                    noMatches()
                } else {
                    matches(matchList)
                }
            }
        }

        fun loadTeams() {
            GlobalScope.launch {
                val databaseConnection = FirebaseDBConnection()
                val teamList = databaseConnection.findTeams(user)
                withContext(Dispatchers.Main) {
                    if (teamList.isEmpty()) {
                        noTeams()
                    } else {
                        teams(teamList)
                    }
                }
            }
        }
        loadTeams()

        matchesBtn.setOnClickListener {
            matchesBtn.setBackgroundResource(R.drawable.rounded_button)
            tournamentsBtn.setBackgroundResource(R.drawable.rounded_button_black_nostroke)
            pastBtn.setBackgroundResource(R.drawable.rounded_button_black_nostroke)
            matchesTxt.setTextColor(Color.parseColor("#FFFFFF"))
            tournamentsTxt.setTextColor(Color.parseColor("#4F4F4F"))
            pastTxt.setTextColor(Color.parseColor("#4F4F4F"))
            loadMatches()
        }

        tournamentsBtn.setOnClickListener {
            tournamentsBtn.setBackgroundResource(R.drawable.rounded_button)
            matchesBtn.setBackgroundResource(R.drawable.rounded_button_black_nostroke)
            pastBtn.setBackgroundResource(R.drawable.rounded_button_black_nostroke)
            tournamentsTxt.setTextColor(Color.parseColor("#FFFFFF"))
            matchesTxt.setTextColor(Color.parseColor("#4F4F4F"))
            pastTxt.setTextColor(Color.parseColor("#4F4F4F"))
        }

        pastBtn.setOnClickListener {
            pastBtn.setBackgroundResource(R.drawable.rounded_button)
            matchesBtn.setBackgroundResource(R.drawable.rounded_button_black_nostroke)
            tournamentsBtn.setBackgroundResource(R.drawable.rounded_button_black_nostroke)
            pastTxt.setTextColor(Color.parseColor("#FFFFFF"))
            matchesTxt.setTextColor(Color.parseColor("#4F4F4F"))
            tournamentsTxt.setTextColor(Color.parseColor("#4F4F4F"))
            loadPastMatches()
        }
    }
}