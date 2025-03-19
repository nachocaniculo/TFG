package com.astradevelop.playconnect

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class HomeTeamsRV(
    private val items: MutableList<Team>,
    private val homeActivity: HomeActivity,
    private val user: String
) : RecyclerView.Adapter<HomeTeamsRV.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.sportText)
        val sportText: TextView = view.findViewById(R.id.dateText)
        val sportIcon: ImageView = view.findViewById(R.id.sportIcon)
        val playersText: TextView = view.findViewById(R.id.playersTxt)
        val sportButton: LinearLayout = view.findViewById(R.id.sportButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_team, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val teamId = items[position].id
        val teamName = items[position].name
        val sport = items[position].sport.toString()
        val captain = items[position].captain
        val players = items[position].players
        holder.nameText.text = teamName
        holder.playersText.text = players.size.toString()
        holder.sportButton.setOnClickListener {
            val intent = Intent(homeActivity, TeamActivity::class.java)
            intent.putExtra("teamInfo", teamId)
            homeActivity.startActivity(intent)
        }

        when (sport){
            "0" -> {holder.sportIcon.setImageResource(R.drawable.padelicon)
                holder.sportText.text = "Padel"}
            "1" -> {holder.sportIcon.setImageResource(R.drawable.tennisicon)
                holder.sportText.text = "Tennis"}
            "2" -> {holder.sportIcon.setImageResource(R.drawable.basketicon)
                holder.sportText.text = "Basketball"}
            "3" -> {holder.sportIcon.setImageResource(R.drawable.footballicon)
                holder.sportText.text = "Football"}
        }
    }

    override fun getItemCount(): Int = items.size
}

