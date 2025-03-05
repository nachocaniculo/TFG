package com.astradevelop.playconnect

import android.content.Context
import android.content.Intent
import android.util.Patterns
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class FirebaseDBConnection {

    //Function to login with email and password
    fun loginAuth(context: Context, email: String, password: String) {
        val auth = FirebaseAuth.getInstance()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                context,
                "Error: Fill in all the fields",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val intent = Intent(context, HomeActivity::class.java)
                        val sharedPref = context.getSharedPreferences(
                            "playconnectlogintoken",
                            Context.MODE_PRIVATE
                        )
                        val editor = sharedPref.edit()
                        editor.putString("userUID", user?.uid)
                        editor.apply()
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(
                            context,
                            "Error: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }


    //Function to register with email and password
    fun registerAuth(context:Context, email: String, password: String, name: String, username: String) {
        val auth = FirebaseAuth.getInstance()

        
        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || username.isEmpty()) {
            Toast.makeText(
                context,
                "Error: Fill in all the fields",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(
                context,
                "Error: Invalid email format",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(
                context,
                "Error: Password must be at least 6 characters",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser

                    user?.let {
                        val intent = Intent(context, ProfilePictureActivity::class.java)

                        val sharedPref = context.getSharedPreferences("playconnectlogintoken", Context.MODE_PRIVATE)
                        val editor = sharedPref.edit()
                        editor.putString("userUID", it.uid)
                        editor.apply()

                        val usuario = hashMapOf(
                            "email" to email,
                            "name" to name,
                            "username" to username,
                            "picture" to "1"
                        )

                        val documentId = it.uid
                        val db = FirebaseFirestore.getInstance()
                        db.collection("players").document(documentId)
                            .set(usuario)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    context.startActivity(intent)
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Error saving user data: ${dbTask.exception?.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    context,
                                    "Error saving user data: $e",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }

                } else {
                    Toast.makeText(
                        context,
                        "Error: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    context,
                    "Error: $e",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    fun updateDocumentPicture(
        context: Context,
        documentId: String,
        value: String
    ) {
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection("players").document(documentId)

        val updates = hashMapOf<String, Any>(
            "picture" to value
        )

        documentRef.update(updates)
            .addOnSuccessListener {
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            }
    }

    suspend fun findMatches(userId: String): ArrayList<ArrayList<String>> {
        val matchData = ArrayList<ArrayList<String>>()

        val query1 = FirebaseFirestore.getInstance()
            .collection("match")
            .whereEqualTo("team1", userId)
        val documents1 = query1.get().await()

        for (document in documents1) {
            matchData.add(
                ArrayList(
                    mutableListOf(
                        document.id,
                        document.getString("name")!!,
                        document.getDate("date").toString(),
                        document.getString("place")!!,
                        document.getString("team1")!!,
                        document.get("sport").toString()
                    )
                )
            )
        }

        val query2 = FirebaseFirestore.getInstance()
            .collection("match")
            .whereEqualTo("team2", userId)
        val documents2 = query2.get().await()

        for (document in documents2) {
            matchData.add(
                ArrayList(
                    mutableListOf(
                        document.id,
                        document.getString("name")!!,
                        document.getDate("date").toString(),
                        document.getString("place")!!,
                        document.getString("team1")!!,
                        document.get("sport").toString()
                    )
                )
            )
        }

        return matchData
    }

    suspend fun findMatchesBySport(sport: Int): ArrayList<ArrayList<String>> {
        val matchData = ArrayList<ArrayList<String>>()

        val query1 = FirebaseFirestore.getInstance()
            .collection("match")
            .whereEqualTo("sport", sport)
        val documents1 = query1.get().await()

        for (document in documents1) {
            if (document.getString("team2")!! == "") {
                matchData.add(
                    ArrayList(
                        mutableListOf(
                            document.id,
                            document.getString("name")!!,
                            document.getDate("date").toString(),
                            document.getString("place")!!
                        )
                    )
                )
            }
        }

        return matchData
    }

    suspend fun findMatchesByName(search: String): ArrayList<ArrayList<String>> {
        val matchData = ArrayList<ArrayList<String>>()

        val query1 = FirebaseFirestore.getInstance()
            .collection("match")
            .whereGreaterThanOrEqualTo("name", search)
            .whereLessThanOrEqualTo("name", search + "\uf8ff")
        val documents1 = query1.get().await()

        for (document in documents1) {
            if (document.getString("team2")!! == "") {
                matchData.add(
                    ArrayList(
                        mutableListOf(
                            document.id,
                            document.getString("name")!!,
                            document.getDate("date").toString(),
                            document.getString("place")!!
                        )
                    )
                )
            }
        }

        return matchData
    }

    fun updateTeam2(
        documentId: String,
        value: String
    ) {
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection("match").document(documentId)

        val updates = hashMapOf<String, Any>(
            "team2" to value
        )

        documentRef.update(updates)
            .addOnSuccessListener {
            }
    }

    fun updateDate(
        documentId: String,
        date: Timestamp
    ) {
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection("match").document(documentId)

        val updates = hashMapOf<String, Any>(
            "date" to date
        )
        documentRef.update(updates)
            .addOnSuccessListener {
            }
    }

    suspend fun findTeams(userId: String): ArrayList<ArrayList<String>> {
        val matchData = ArrayList<ArrayList<String>>()

        val query1 = FirebaseFirestore.getInstance()
            .collection("teams")
            .whereEqualTo("captain", userId)
        val documents1 = query1.get().await()

        for (document in documents1) {
            matchData.add(
                ArrayList(
                    mutableListOf(
                        document.id,
                        document.getString("name")!!,
                        document.get("sport").toString(),
                        document.getString("captain")!!,
                        (document.get("players") as? List<*>)!!.joinToString(",")
                    )
                )
            )
        }

        val query2 = FirebaseFirestore.getInstance()
            .collection("teams")
            .whereNotEqualTo("captain", userId)
        val documents2 = query2.get().await()

        for (document in documents2) {
            val players = document.get("players") as? List<*>
            for (player in players!!){
                if (userId == player.toString()){
                    matchData.add(
                        ArrayList(
                            mutableListOf(
                                document.id,
                                document.getString("name")!!,
                                document.get("sport").toString(),
                                document.getString("captain")!!,
                                players.joinToString(",")
                            )
                        )
                    )
                }
            }
        }

        return matchData
    }

    fun addPlayerToTeam(
        documentId: String,
        playerId: String,
        searchActivity: SearchActivity
    ) {
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection("teams").document(documentId)
        var isOnTeam = false
        documentRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val players = document.get("players") as? List<*>
                for (player in players!!){
                    if (player.toString() == playerId){
                        isOnTeam = true
                    }
                }
                if (!isOnTeam) {
                    var playerList = players.joinToString(",")
                    playerList = "$playerList,$playerId"
                    updatePlayersInTeamV2(documentId, playerList, searchActivity)
                } else {
                    Toast.makeText(
                        searchActivity,
                        "You have already joined this team.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    searchActivity,
                    "This code doesn't belong to any team.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun updatePlayersInTeam(
        documentId: String,
        players: String
    ) {
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection("teams").document(documentId)
        val playerList = players.split(",")

        val updates = hashMapOf<String, Any>(
            "players" to playerList
        )
        documentRef.update(updates)
            .addOnSuccessListener {
            }
    }

    fun updatePlayersInTeamV2(
        documentId: String,
        players: String,
        searchActivity: SearchActivity
    ) {
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection("teams").document(documentId)
        val playerList = players.split(",")

        val updates = hashMapOf<String, Any>(
            "players" to playerList
        )
        documentRef.update(updates)
            .addOnSuccessListener {
                Toast.makeText(
                    searchActivity,
                    "Joined!",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}