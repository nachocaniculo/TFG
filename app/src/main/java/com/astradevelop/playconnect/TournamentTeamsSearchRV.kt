package com.astradevelop.playconnect

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.ArrayList

class TournamentTeamsSearchRV(
    private val items: ArrayList<Team>,
    private val matchActivity: TournamentActivity
) : RecyclerView.Adapter<TournamentTeamsSearchRV.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.sportText)
        val sportIcon: ImageView = view.findViewById(R.id.profilePic)
        val button: Button = view.findViewById(R.id.button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_team_create, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val teamName = items[position].name
        val id = items[position].id
        val sport = items[position].sport.toString()
        holder.nameText.text = teamName

        when (sport){
            "0" -> holder.sportIcon.setImageResource(R.drawable.padelicon)
            "1" -> holder.sportIcon.setImageResource(R.drawable.tennisicon)
            "2" -> holder.sportIcon.setImageResource(R.drawable.basketicon)
            "3" -> holder.sportIcon.setImageResource(R.drawable.footballicon)
        }

        holder.button.setOnClickListener {
            matchActivity.chooseTeam(id)
        }
    }

    override fun getItemCount(): Int = items.size
}

