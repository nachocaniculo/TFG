package com.astradevelop.playconnect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class TeamMatchesRV(
    private val items: ArrayList<Match>,
    private val teamId: String
) : RecyclerView.Adapter<TeamMatchesRV.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val sportText: TextView = view.findViewById(R.id.sportText)
        val dateText: TextView = view.findViewById(R.id.dateText)
        val locationText: TextView = view.findViewById(R.id.locationText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_team_match, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (items[position].players.size == 2) {
            for (team in items[position].players) {
                if (team != teamId) {
                    val db = FirebaseFirestore.getInstance()
                    db.collection("teams").document(team.toString()).get()
                        .addOnSuccessListener { document ->
                            val nameTemp = document.getString("name") ?: ""
                            holder.sportText.text = nameTemp
                        }.addOnFailureListener {
                            holder.sportText.text = "Pending rival"
                        }
                }
            }
        } else {
            holder.sportText.text = "Pending rival"
        }

        val timestamp: Timestamp = items[position].date

        val date = timestamp.toDate()

        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val formattedDate = dateFormat.format(date)

        holder.dateText.text = formattedDate

        holder.locationText.text = items[position].place
    }

    override fun getItemCount(): Int = items.size
}

