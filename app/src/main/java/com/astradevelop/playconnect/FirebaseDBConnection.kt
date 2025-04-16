package com.astradevelop.playconnect

import android.content.Context
import android.content.Intent
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date


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

    suspend fun findMatches(userId: String): ArrayList<Match> = withContext(Dispatchers.IO) {
        val db = FirebaseFirestore.getInstance()
        val result = db.collection("match").get().await()
        val matchData = ArrayList<Match>()

        for (document in result) {
            val players = document.get("players") as? List<*> ?: emptyList<Any>()
            if (userId in players) {
                val timestamp = document.get("date") as Timestamp
                val date = timestamp.toDate()
                val currentDate = Date()
                if (date > currentDate) {
                    val matchEntry = Match(
                        document.id,
                        timestamp,
                        document.getString("name") ?: "",
                        document.getString("description") ?: "",
                        document.getString("maxPlayers") ?: "",
                        document.getString("place") ?: "",
                        players,
                        document.getLong("sport") ?: 0,
                        document.getLong("type") ?: 0,
                        document.get("ratedPlayer") as? List<String> ?: emptyList<String>()
                    )
                    matchData.add(matchEntry)
                }
            }
        }

        matchData
    }

    suspend fun findPastMatches(userId: String): ArrayList<Match> {
        val matchData = ArrayList<Match>()
        val db = FirebaseFirestore.getInstance()

        val result = db.collection("match").get().await()

        for (document in result) {
            val players = document.get("players") as? List<*> ?: emptyList<Any>()
            if (userId in players) {
                val timestamp = document.get("date") as Timestamp
                val date = timestamp.toDate()
                val currentDate = Date()
                if (date <= currentDate) {
                    val matchEntry = Match(
                        document.id,
                        timestamp,
                        document.getString("name") ?: "",
                        document.getString("description") ?: "",
                        document.getString("maxPlayers") ?: "",
                        document.getString("place") ?: "",
                        players,
                        document.getLong("sport") ?: 0,
                        document.getLong("type") ?: 0,
                        document.get("ratedPlayer") as? List<String> ?: emptyList<String>()
                    )
                    matchData.add(matchEntry)
                }
            }
        }
        return matchData
    }

    suspend fun findMatchesBySport(sport: Int, userId: String, type: Long): ArrayList<Match> {
        val matchData = ArrayList<Match>()
        val db = FirebaseFirestore.getInstance()

        try {
            val result = db.collection("match").get().await()
            for (document in result) {
                val players = document.get("players") as? List<*> ?: emptyList<Any>()
                val maxPlayers = document.get("maxPlayers")?.toString()?.toIntOrNull() ?: Int.MAX_VALUE

                if (document.get("sport").toString().toIntOrNull() == sport &&
                    userId !in players && players.size < maxPlayers && document.getLong("type")!! == type) {

                    val matchEntry = Match (
                        document.id,
                        document.get("date") as Timestamp,
                        document.getString("name")!!,
                        document.getString("description")!!,
                        document.getString("maxPlayers")!!,
                        document.getString("place")!!,
                        document.get("players") as? List<*> ?: emptyList<Any>(),
                        document.getLong("sport")!!,
                        document.getLong("type")!!,
                        document.get("ratedPlayer") as? List<String> ?: emptyList<String>()
                    )

                    matchData.add(matchEntry)
                }
            }
        } catch (_: Exception) {
        }

        return matchData
    }

    suspend fun findMatchesByName(search: String, userId: String, type:Long): ArrayList<Match> {
        val matchData = ArrayList<Match>()

        val query1 = FirebaseFirestore.getInstance()
            .collection("match")
            .whereGreaterThanOrEqualTo("name", search)
            .whereLessThanOrEqualTo("name", search + "\uf8ff")
        val documents1 = query1.get().await()

        for (document in documents1) {
            val players = document.get("players") as? List<*> ?: emptyList<Any>()
            val maxPlayers = document.get("maxPlayers")?.toString()?.toIntOrNull() ?: Int.MAX_VALUE
            if (userId !in players && players.size < maxPlayers && document.getLong("sport")!! == type) {

                val matchEntry = Match (
                    document.id,
                    document.get("date") as Timestamp,
                    document.getString("name")!!,
                    document.getString("description")!!,
                    document.getString("maxPlayers")!!,
                    document.getString("place")!!,
                    document.get("players") as? List<*> ?: emptyList<Any>(),
                    document.getLong("sport")!!,
                    document.getLong("type")!!,
                    document.get("ratedPlayer") as? List<String> ?: emptyList<String>()
                )

                matchData.add(matchEntry)
            }
        }

        return matchData
    }

    fun updateTeam2(documentId: String, userId: String) {
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection("match").document(documentId)

        documentRef.get().addOnSuccessListener { result ->
            val players = result.get("players") as? MutableList<*> ?: mutableListOf<Any>()

            if (players.contains(userId)) {
                val updatedPlayers = players.filter { it != userId }

                if (updatedPlayers.isEmpty()) {
                    documentRef.delete()
                } else {
                    documentRef.update("players", updatedPlayers)
                }
            }
        }
    }

    fun updateTeam(documentId: String, userId: String) {
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection("match").document(documentId)

        documentRef.get().addOnSuccessListener { result ->
            val players = (result.get("players") as? List<*>)?.mapNotNull { it as? String }?.toMutableList() ?: mutableListOf()
            players.add(userId)
            documentRef.update("players", players)
        }
    }

    fun updateDateAndLocation(
        documentId: String,
        date: Timestamp,
        location: String
    ) {
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection("match").document(documentId)

        val updates = hashMapOf<String, Any>(
            "date" to date,
            "place" to location
        )
        documentRef.update(updates)
            .addOnSuccessListener {
            }
    }

    suspend fun findTeams(userId: String): ArrayList<Team> {
        val teamData = ArrayList<Team>()

        val query1 = FirebaseFirestore.getInstance()
            .collection("teams")
            .whereEqualTo("captain", userId)
        val documents1 = query1.get().await()

        for (document in documents1) {
            teamData.add(
                Team(
                    document.id,
                    document.getString("name")!!,
                    document.getString("captain")!!,
                    document.getString("maxPlayers")!!,
                    document.getLong("sport")!!,
                    document.get("players") as? List<*> ?: emptyList<Any>()
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
                    teamData.add(
                        Team(
                            document.id,
                            document.getString("name")!!,
                            document.getString("captain")!!,
                            document.getString("maxPlayers")!!,
                            document.getLong("sport")!!,
                            document.get("players") as? List<*> ?: emptyList<Any>()
                        )
                    )
                }
            }
        }

        return teamData
    }


    suspend fun findTeamById(id: String): Team? {
        val db = FirebaseFirestore.getInstance()

        return try {
            val document = db.collection("teams").document(id).get().await()

            if (document.exists()) {
                Team(
                    id = document.id,
                    name = document.getString("name") ?: "",
                    captain = document.getString("captain") ?: "",
                    maxPlayers = document.getString("maxPlayers") ?: "",
                    sport = document.getLong("sport") ?: 0L,
                    players = document.get("players") as? List<String> ?: emptyList()
                )
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun findMatchById(id: String): Match? {
        val db = FirebaseFirestore.getInstance()

        return try {
            val document = db.collection("match").document(id).get().await()

            if (document.exists()) {
                Match (
                    document.id,
                    document.get("date") as Timestamp,
                    document.getString("name")!!,
                    document.getString("description")!!,
                    document.getString("maxPlayers")!!,
                    document.getString("place")!!,
                    document.get("players") as? List<*> ?: emptyList<Any>(),
                    document.getLong("sport")!!,
                    document.getLong("type")!!,
                    document.get("ratedPlayer") as? List<String> ?: emptyList<String>()
                )
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
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
        players: MutableList<*>
    ) {
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection("teams").document(documentId)

        val updates = hashMapOf<String, Any>(
            "players" to players
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

    fun updatePlayersInMatch(
        documentId: String,
        players: MutableList<*>
    ) {
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection("match").document(documentId)

        val updates = hashMapOf<String, Any>(
            "players" to players
        )
        documentRef.update(updates)
            .addOnSuccessListener {
            }
    }

    fun updatePlayerInfo(
        player: Player
    ) {
        val documentId = player.id
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection("players").document(documentId)

        val updates = hashMapOf<String, Any>(
            "email" to player.email,
            "name" to player.name,
            "username" to player.username,
            "ratings" to player.ratings
        )

        documentRef.update(updates)
            .addOnSuccessListener {
            }
    }

    suspend fun findTeamsByPlayerAndSport(userId: String, sport: Long): ArrayList<Team> {
        val teamData = ArrayList<Team>()

        val query1 = FirebaseFirestore.getInstance()
            .collection("teams")
            .whereEqualTo("captain", userId)
        val documents1 = query1.get().await()

        for (document in documents1) {
            val sportTemp = document.getLong("sport")!!
            if (sport == sportTemp) {
                teamData.add(
                    Team(
                        document.id,
                        document.getString("name")!!,
                        document.getString("captain")!!,
                        document.getString("maxPlayers")!!,
                        document.getLong("sport")!!,
                        document.get("players") as? List<*> ?: emptyList<Any>()
                    )
                )
            }
        }

        val query2 = FirebaseFirestore.getInstance()
            .collection("teams")
            .whereNotEqualTo("captain", userId)
        val documents2 = query2.get().await()

        for (document in documents2) {
            val sportTemp = document.getLong("sport")!!
            if (sport == sportTemp) {
                val players = document.get("players") as? List<*>
                for (player in players!!) {
                    if (userId == player.toString()) {
                        teamData.add(
                            Team(
                                document.id,
                                document.getString("name")!!,
                                document.getString("captain")!!,
                                document.getString("maxPlayers")!!,
                                document.getLong("sport")!!,
                                document.get("players") as? List<*> ?: emptyList<Any>()
                            )
                        )
                    }
                }
            }
        }

        return teamData
    }

    suspend fun findMatchesByTeam(teamID: String): ArrayList<Match> {
        val matchData = ArrayList<Match>()

        val query1 = FirebaseFirestore.getInstance()
            .collection("match")
        val documents1 = query1.get().await()

        for (document in documents1) {
            val type = document.getLong("type")!!
            if (type.toInt() == 2) {
                val teams = document.get("players") as? List<*>
                for (team in teams!!) {
                    if (teamID == team.toString()) {
                        val timestamp = document.get("date") as Timestamp
                        val date = timestamp.toDate()
                        val currentDate = Date()
                        if (date > currentDate) {
                            matchData.add(
                                Match(
                                    document.id,
                                    document.get("date") as Timestamp,
                                    document.getString("name")!!,
                                    document.getString("description")!!,
                                    document.getString("maxPlayers")!!,
                                    document.getString("place")!!,
                                    document.get("players") as? List<*> ?: emptyList<Any>(),
                                    document.getLong("sport")!!,
                                    document.getLong("type")!!,
                                    document.get("ratedPlayer") as? List<String>
                                        ?: emptyList<String>()
                                )
                            )
                        }
                    }
                }
            }
        }

        return matchData
    }

    suspend fun findPastMatchesByTeam(teamID: String): ArrayList<Match> {
        val matchData = ArrayList<Match>()

        val query1 = FirebaseFirestore.getInstance()
            .collection("match")
        val documents1 = query1.get().await()

        for (document in documents1) {
            val type = document.getLong("type")!!
            if (type.toInt() == 2) {
                val teams = document.get("players") as? List<*>
                for (team in teams!!) {
                    if (teamID == team.toString()) {
                        val timestamp = document.get("date") as Timestamp
                        val date = timestamp.toDate()
                        val currentDate = Date()
                        if (date <= currentDate) {
                            matchData.add(
                                Match(
                                    document.id,
                                    document.get("date") as Timestamp,
                                    document.getString("name")!!,
                                    document.getString("description")!!,
                                    document.getString("maxPlayers")!!,
                                    document.getString("place")!!,
                                    document.get("players") as? List<*> ?: emptyList<Any>(),
                                    document.getLong("sport")!!,
                                    document.getLong("type")!!,
                                    document.get("ratedPlayer") as? List<String>
                                        ?: emptyList<String>()
                                )
                            )
                        }
                    }
                }
            }
        }

        return matchData
    }

    fun ratePlayer(playerID: String, rate: Int){
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("players").document(playerID)

        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val currentArray = document.get("ratings") as? MutableList<Int> ?: mutableListOf()

                    currentArray.add(rate)

                    docRef.update("ratings", currentArray)
                }
            }
    }

    fun endMatch(matchID: String, playerID: String){
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("match").document(matchID)

        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val currentArray = document.get("ratedPlayer") as? MutableList<String> ?: mutableListOf()

                    currentArray.add(playerID)

                    docRef.update("ratedPlayer", currentArray)
                }
            }
    }
}