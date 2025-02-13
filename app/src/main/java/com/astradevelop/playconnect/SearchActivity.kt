package com.astradevelop.playconnect

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchActivity : AppCompatActivity() {
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
        val user = sharedPref.getString("userUID", "")

        val backBtn: ImageView = findViewById(R.id.arrow1)
        backBtn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        var sportSelected = false
        var sport = 0

        val matchesRV: RecyclerView = findViewById(R.id.matchesRV)
        val noResultsText: TextView = findViewById(R.id.noResultsText)

        fun updateMatches(matchList: ArrayList<ArrayList<String>>) {
            matchesRV.layoutManager = LinearLayoutManager(this)
            matchesRV.adapter = SearchMatchesRV(matchList, user!!, this)
            matchesRV.visibility = View.VISIBLE
            noResultsText.visibility = View.GONE
        }

        fun searchBySport(){
            GlobalScope.launch {
                val databaseConnection = FirebaseDBConnection()
                val matchList = databaseConnection.findMatchesBySport(sport)
                withContext(Dispatchers.Main) {
                    if (matchList.isNotEmpty()) {
                        updateMatches(matchList)
                    }
                }
            }
        }

        val sportBg: TextView = findViewById(R.id.sportBg)
        val padelBtn: LinearLayout = findViewById(R.id.padelButton)
        val padelText: TextView = findViewById(R.id.padelText)
        val padelIcon: ImageView = findViewById(R.id.padelIcon)
        val arrow4: ImageView = findViewById(R.id.arrow4)

        val sportText: TextView = findViewById(R.id.sportText)
        val sportIcon: ImageView = findViewById(R.id.sportIcon)

        val sportBtn: LinearLayout = findViewById(R.id.sportButton)
        sportBtn.setOnClickListener {
            sportBg.visibility = View.VISIBLE
            padelBtn.visibility = View.VISIBLE
            padelText.visibility = View.VISIBLE
            padelIcon.visibility = View.VISIBLE
            arrow4.visibility = View.VISIBLE
        }

        padelBtn.setOnClickListener {
            sportBg.visibility = View.GONE
            padelBtn.visibility = View.GONE
            padelText.visibility = View.GONE
            padelIcon.visibility = View.GONE
            arrow4.visibility = View.GONE
            sportText.text = "Padel"
            sportIcon.setImageResource(R.drawable.padelicon)
            sportSelected = true
            sport = 0
            searchBySport()
        }
    }
}