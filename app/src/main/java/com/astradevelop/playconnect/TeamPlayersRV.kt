package com.astradevelop.playconnect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TeamPlayersRV(
    private val items: MutableList<String>,
    private val playerList: MutableList<String>,
    private val teamId: String,
    private val user: String?,
    private val captain: String
) : RecyclerView.Adapter<TeamPlayersRV.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.sportText)
        val deleteBtn: ImageView = view.findViewById(R.id.imageView2)
        val crownIcon: ImageView = view.findViewById(R.id.Crown)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_player, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameText.text = items[position]
        val player = playerList[position]
        if (player == captain){
            holder.crownIcon.visibility = View.VISIBLE
        }
        if (user == captain){
            if (player != user){
                holder.deleteBtn.visibility = View.VISIBLE
            }
        }
        holder.deleteBtn.setOnClickListener{
            playerList.remove(player)
            items.removeAt(position)
            val databaseConnection = FirebaseDBConnection()
            databaseConnection.updatePlayersInTeam(teamId, playerList)
            notifyItemRemoved(position)
        }
    }

    override fun getItemCount(): Int = items.size
}

