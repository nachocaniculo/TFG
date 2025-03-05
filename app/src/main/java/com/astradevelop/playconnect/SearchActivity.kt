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

    private lateinit var searchText: EditText

    lateinit var matchesRV: RecyclerView
    lateinit var noResultsText: TextView

    private var user = ""
    @SuppressLint("MissingInflatedId")
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

        searchText = findViewById(R.id.searchText)

        matchesRV = findViewById(R.id.matchesRV)
        noResultsText = findViewById(R.id.noResultsText)

        val teamBtn: LinearLayout = findViewById(R.id.teamTV)
        val eventBtn: LinearLayout = findViewById(R.id.eventTV)

        val teamTxt: TextView = findViewById(R.id.teamText)
        val eventTxt: TextView = findViewById(R.id.scheduledText)

        val searchCL: ConstraintLayout = findViewById(R.id.searchCL)
        val teamCL: ConstraintLayout = findViewById(R.id.teamCL)

        val addBtnBg: ImageView = findViewById(R.id.addBtnBg)

        teamBtn.setOnClickListener {
            teamBtn.setBackgroundResource(R.drawable.rounded_button)
            eventBtn.setBackgroundResource(R.drawable.rounded_button_black_nostroke)
            teamTxt.setTextColor(Color.parseColor("#FFFFFF"))
            eventTxt.setTextColor(Color.parseColor("#4F4F4F"))
            searchCL.visibility = View.GONE
            teamCL.visibility = View.VISIBLE
            addBtnBg.visibility = View.VISIBLE
        }

        eventBtn.setOnClickListener {
            teamBtn.setBackgroundResource(R.drawable.rounded_button_black_nostroke)
            eventBtn.setBackgroundResource(R.drawable.rounded_button)
            eventTxt.setTextColor(Color.parseColor("#FFFFFF"))
            teamTxt.setTextColor(Color.parseColor("#4F4F4F"))
            searchCL.visibility = View.VISIBLE
            teamCL.visibility = View.GONE
            addBtnBg.visibility = View.GONE
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

        fun searchByName(){
            GlobalScope.launch {
                val databaseConnection = FirebaseDBConnection()
                val matchList = databaseConnection.findMatchesByName(searchText.text.toString())
                withContext(Dispatchers.Main) {
                    if (matchList.isNotEmpty()) {
                        updateMatches(matchList)
                    }
                }
            }
        }

        searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                if (editable!!.isNotEmpty()) {
                    searchByName()
                } else{
                    matchesRV.visibility = View.GONE
                    noResultsText.visibility = View.VISIBLE
                }
            }
        })

        val sportBtn: LinearLayout = findViewById(R.id.sportButton)
        sportBtn.setOnClickListener {
            sportBg.visibility = View.VISIBLE
            sportRV.visibility = View.VISIBLE
            sportRV.layoutManager = LinearLayoutManager(this)
            sportRV.adapter = SportsSearchRV(this)
        }
    }

    private fun updateMatches(matchList: ArrayList<ArrayList<String>>) {
        if (matchList.isEmpty()){
            matchesRV.visibility = View.GONE
            noResultsText.visibility = View.VISIBLE
        } else {
            matchesRV.layoutManager = LinearLayoutManager(this)
            matchesRV.adapter = SearchMatchesRV(matchList, user!!, this, sport)
            matchesRV.visibility = View.VISIBLE
            noResultsText.visibility = View.GONE
        }
    }

    private fun searchBySport(){
        GlobalScope.launch {
            val databaseConnection = FirebaseDBConnection()
            val matchList = databaseConnection.findMatchesBySport(sport)
            withContext(Dispatchers.Main) {
                updateMatches(matchList)
            }
        }
    }

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
        searchBySport()

    }
}