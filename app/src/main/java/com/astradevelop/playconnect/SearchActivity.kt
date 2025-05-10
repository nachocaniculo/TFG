package com.astradevelop.playconnect

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.Constraint
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchActivity : AppCompatActivity() {

    private lateinit var sportBg: TextView
    private lateinit var sportRV: RecyclerView

    lateinit var sportText: TextView
    private lateinit var sportIcon: ImageView

    private var sportSelected = false
    private var sport = 0

    lateinit var matchesRV: RecyclerView
    lateinit var noResultsText: TextView

    private var user = ""

    private var matchType = 1

    private var type = 0
    @SuppressLint("MissingInflatedId", "SetTextI18n", "UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val sharedPref = getSharedPreferences("playconnectlogintoken", Context.MODE_PRIVATE)
        user = sharedPref.getString("userUID", "").toString()

        val backBtn: ImageView = findViewById(R.id.arrow1)
        backBtn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        sportBg = findViewById(R.id.sportBg)
        sportRV = findViewById(R.id.sportsRV)

        sportText = findViewById(R.id.sportText)
        sportIcon = findViewById(R.id.sportIcon)

        matchesRV = findViewById(R.id.matchesRV)
        noResultsText = findViewById(R.id.noResultsText)

        val teamBtn: LinearLayout = findViewById(R.id.teamTV)
        val eventBtn: LinearLayout = findViewById(R.id.eventTV)
        val tournamentBtn: LinearLayout = findViewById(R.id.tournamentsTV)

        val teamTxt: TextView = findViewById(R.id.teamText)
        val eventTxt: TextView = findViewById(R.id.scheduledText)
        val tournamentsTxt: TextView = findViewById(R.id.tournamentsText)

        val searchCL: ConstraintLayout = findViewById(R.id.searchCL)
        val teamCL: ConstraintLayout = findViewById(R.id.teamCL)

        val addBtnBg: ImageView = findViewById(R.id.addBtnBg)

        val teamSwitch: Switch = findViewById(R.id.switch2)
        val teamOrPlayersText: TextView = findViewById(R.id.teamOrPlayersText)
        teamSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (type == 0) {
                    teamOrPlayersText.text = "Search team matches"
                    matchType = 2
                    searchBySport()
                } else {
                    teamOrPlayersText.text = "Search team tournaments"
                    matchType = 2
                    searchTournament()
                }
            } else {
                if (type == 0) {
                    teamOrPlayersText.text = "Search player matches"
                    matchType = 1
                    searchBySport()
                } else {
                    teamOrPlayersText.text = "Search player tournaments"
                    matchType = 1
                    searchTournament()
                }
            }
        }

        eventBtn.setOnClickListener {
            eventBtn.setBackgroundResource(R.drawable.rounded_button)
            teamBtn.setBackgroundResource(R.drawable.rounded_button_black_nostroke)
            tournamentBtn.setBackgroundResource(R.drawable.rounded_button_black_nostroke)
            eventTxt.setTextColor(Color.parseColor("#FFFFFF"))
            teamTxt.setTextColor(Color.parseColor("#4F4F4F"))
            tournamentsTxt.setTextColor(Color.parseColor("#4F4F4F"))
            searchCL.visibility = View.VISIBLE
            teamCL.visibility = View.GONE
            addBtnBg.visibility = View.GONE
            type = 0
            if (teamSwitch.isChecked) {
                teamOrPlayersText.text = "Search team matches"
            } else {
                teamOrPlayersText.text = "Search player matches"
            }
            searchBySport()
        }

        tournamentBtn.setOnClickListener {
            tournamentBtn.setBackgroundResource(R.drawable.rounded_button)
            eventBtn.setBackgroundResource(R.drawable.rounded_button_black_nostroke)
            teamBtn.setBackgroundResource(R.drawable.rounded_button_black_nostroke)
            tournamentsTxt.setTextColor(Color.parseColor("#FFFFFF"))
            eventTxt.setTextColor(Color.parseColor("#4F4F4F"))
            teamTxt.setTextColor(Color.parseColor("#4F4F4F"))
            searchCL.visibility = View.VISIBLE
            teamCL.visibility = View.GONE
            addBtnBg.visibility = View.GONE
            type = 1
            if (teamSwitch.isChecked) {
                teamOrPlayersText.text = "Search team tournaments"
            } else {
                teamOrPlayersText.text = "Search player tournaments"
            }
            searchTournament()
        }

        teamBtn.setOnClickListener {
            teamBtn.setBackgroundResource(R.drawable.rounded_button)
            eventBtn.setBackgroundResource(R.drawable.rounded_button_black_nostroke)
            tournamentBtn.setBackgroundResource(R.drawable.rounded_button_black_nostroke)
            teamTxt.setTextColor(Color.parseColor("#FFFFFF"))
            eventTxt.setTextColor(Color.parseColor("#4F4F4F"))
            tournamentsTxt.setTextColor(Color.parseColor("#4F4F4F"))
            searchCL.visibility = View.GONE
            teamCL.visibility = View.VISIBLE
            addBtnBg.visibility = View.VISIBLE
            type = 2
        }

        val teamText: EditText = findViewById(R.id.teamInviteText)

        addBtnBg.setOnClickListener {
            if (teamText.text.isNotEmpty()){
                val databaseConnection = FirebaseDBConnection()
                databaseConnection.addPlayerToTeam(teamText.text.toString(), user, this)
            } else {
                Toast.makeText(
                    this,
                    "Please enter a valid code.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val sportBtn: LinearLayout = findViewById(R.id.sportButton)
        sportBtn.setOnClickListener {
            sportBg.visibility = View.VISIBLE
            sportRV.visibility = View.VISIBLE
            sportRV.layoutManager = LinearLayoutManager(this)
            sportRV.adapter = SportsSearchRV(this)
        }
    }

    private fun updateMatches(matchList: ArrayList<Match>) {
        if (matchList.isEmpty()){
            matchesRV.visibility = View.GONE
            noResultsText.visibility = View.VISIBLE
        } else {
            matchesRV.layoutManager = LinearLayoutManager(this)
            matchesRV.adapter = SearchMatchesRV(matchList, user, this, sport)
            matchesRV.visibility = View.VISIBLE
            noResultsText.visibility = View.GONE
        }
    }

    private fun updateTournaments(tournamentList: ArrayList<Tournament>) {
        if (tournamentList.isEmpty()){
            matchesRV.visibility = View.GONE
            noResultsText.visibility = View.VISIBLE
        } else {
            matchesRV.layoutManager = LinearLayoutManager(this)
            matchesRV.adapter = SearchTournamentsRV(tournamentList, user, this, sport)
            matchesRV.visibility = View.VISIBLE
            noResultsText.visibility = View.GONE
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun searchBySport(){
        GlobalScope.launch {
            val databaseConnection = FirebaseDBConnection()
            val matchList = databaseConnection.findMatchesBySport(sport, user, matchType.toLong())
            withContext(Dispatchers.Main) {
                updateMatches(matchList)
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun searchTournament(){
        GlobalScope.launch {
            val databaseConnection = FirebaseDBConnection()
            val tournamentList = databaseConnection.findTournaments(sport, user, matchType.toLong())
            withContext(Dispatchers.Main) {
                updateTournaments(tournamentList)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun setSport(sportTemp: Int){
        when (sportTemp){
            0 -> {sportText.text = "Padel"
                sportIcon.setImageResource(R.drawable.padelicon)}
            1 -> {sportText.text = "Tennis"
                sportIcon.setImageResource(R.drawable.tennisicon)}
            2 -> {sportText.text = "Basketball"
                sportIcon.setImageResource(R.drawable.basketicon)}
            3 -> {sportText.text = "Football"
                sportIcon.setImageResource(R.drawable.footballicon)}
        }
        sportBg.visibility = View.GONE
        sportRV.visibility = View.GONE
        sportSelected = true
        sport = sportTemp
        when (type) {
            0 -> searchBySport()
            1 -> searchTournament()
        }

    }
}