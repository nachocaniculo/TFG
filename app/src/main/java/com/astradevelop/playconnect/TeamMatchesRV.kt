package com.astradevelop.playconnect

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

class TeamMatchesRV(
    private val items: ArrayList<Match>
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
            val team2 = items[position].players[1]
            holder.sportText.text = team2.toString()
        } else {
            holder.sportText.text = "Pending rival"
        }
        val sport = items[position].sport.toString()

        val timestamp: Timestamp = items[position].date

        val date = timestamp.toDate()

        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val formattedDate = dateFormat.format(date)

        holder.dateText.text = formattedDate

        holder.locationText.text = items[position].place
    }

    override fun getItemCount(): Int = items.size
}

