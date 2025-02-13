package com.astradevelop.playconnect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SearchMatchesRV(
    private val items: ArrayList<ArrayList<String>>,
    private val user: String,
    private val searchActivity: SearchActivity
) : RecyclerView.Adapter<SearchMatchesRV.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val sportText: TextView = view.findViewById(R.id.sportText)
        val dateText: TextView = view.findViewById(R.id.dateText)
        val locationText: TextView = view.findViewById(R.id.locationText)
        val joinButton: LinearLayout = view.findViewById(R.id.joinButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_match_join, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val matchId = items[position]
        holder.sportText.text = items[position][1]
        holder.dateText.text = items[position][2]
        holder.locationText.text = items[position][3]
        holder.joinButton.setOnClickListener {
            FirebaseDBConnection().updateTeam2(items[position][0], user)
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun getItemCount(): Int = items.size
}

