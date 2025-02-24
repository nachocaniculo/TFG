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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
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

        val profilePic : ImageView = findViewById(R.id.profilePic)
        profilePic.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("user", name)
            intent.putExtra("email", mail)
            intent.putExtra("profilePicture", profilePicture)
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

        val matchesBtn: LinearLayout = findViewById(R.id.scheduledTV)
        val tournamentsBtn: LinearLayout = findViewById(R.id.tournamentsTV)

        val matchesTxt: TextView = findViewById(R.id.scheduledText)
        val tournamentsTxt: TextView = findViewById(R.id.tournamentsText)

        matchesBtn.setOnClickListener {
            matchesBtn.setBackgroundResource(R.drawable.rounded_button)
            tournamentsBtn.setBackgroundResource(R.drawable.rounded_button_black_nostroke)
            matchesTxt.setTextColor(Color.parseColor("#FFFFFF"))
            tournamentsTxt.setTextColor(Color.parseColor("#4F4F4F"))
        }

        tournamentsBtn.setOnClickListener {
            tournamentsBtn.setBackgroundResource(R.drawable.rounded_button)
            matchesBtn.setBackgroundResource(R.drawable.rounded_button_black_nostroke)
            tournamentsTxt.setTextColor(Color.parseColor("#FFFFFF"))
            matchesTxt.setTextColor(Color.parseColor("#4F4F4F"))
        }

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
            finish()
        }

        createTV.setOnClickListener {
            val intent = Intent(this, CreateActivity::class.java)
            startActivity(intent)
            finish()
        }

        val matchesRV: RecyclerView = findViewById(R.id.matchesRV)
        val noResultsText: TextView = findViewById(R.id.noResultsText)

        fun noMatches(){
            noResultsText.visibility = View.VISIBLE
            matchesRV.visibility = View.GONE
        }

        fun matches(matchList: ArrayList<ArrayList<String>>) {
            matchesRV.layoutManager = LinearLayoutManager(this)
            matchesRV.adapter = HomeMatchesRV(matchList, this, user)
        }

        GlobalScope.launch {
            val databaseConnection = FirebaseDBConnection()
            val matchList = databaseConnection.findMatches(user)
            withContext(Dispatchers.Main) {
                if (matchList.isEmpty()) {
                    noMatches()
                } else {
                    matches(matchList)
                }
            }
        }
    }
}