package com.astradevelop.playconnect

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class TournamentMatchesRV(
    private val items: List<TournamentMatch>,
    private val type: Int
) : RecyclerView.Adapter<TournamentMatchesRV.ViewHolder>() {

    val db = FirebaseFirestore.getInstance()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val matchName: TextView = view.findViewById(R.id.sportText)
        val teams: TextView = view.findViewById(R.id.sportText2)
        val round: TextView = view.findViewById(R.id.locationText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_match_tournament, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var team1 = "Team 1"
        var team2 = "Team 2"
        if (type == 1){
            if (items[position].team1.name != null) {
                db.collection("players")
                    .document(items[position].team1.name!!)
                    .get()
                    .addOnSuccessListener { document ->
                        team1 = document.getString("name") ?: "?"
                        holder.teams.text = "$team1 VS $team2"
                    }
            }
            if (items[position].team2.name != null) {
                db.collection("players")
                    .document(items[position].team2.name!!)
                    .get()
                    .addOnSuccessListener { document ->
                        team2 = document.getString("name") ?: "?"
                        holder.teams.text = "$team1 VS $team2"
                    }
            }
        } else if (type == 2) {
            if (items[position].team1.name != null) {
                db.collection("teams")
                    .document(items[position].team1.name!!)
                    .get()
                    .addOnSuccessListener { document ->
                        team1 = document.getString("name") ?: "?"
                        holder.teams.text = "$team1 VS $team2"
                    }
            }
            if (items[position].team2.name != null) {
                db.collection("teams")
                    .document(items[position].team2.name!!)
                    .get()
                    .addOnSuccessListener { document ->
                        team2 = document.getString("name") ?: "?"
                        holder.teams.text = "$team1 VS $team2"
                    }
            }
        }
        holder.teams.text = "$team1 VS $team2"
        holder.matchName.text = "Match ${position+1}"
        holder.round.text = "Round ${items[position].round}"
    }

    override fun getItemCount(): Int = items.size
}

