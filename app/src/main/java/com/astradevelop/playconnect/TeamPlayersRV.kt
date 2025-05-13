package com.astradevelop.playconnect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TeamPlayersRV(
    private val items: MutableList<String>,
    private val playerRatingsList: MutableList<String>,
    private val playerList: MutableList<String>,
    private val teamId: String,
    private val user: String?,
    private val captain: String
) : RecyclerView.Adapter<TeamPlayersRV.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.sportText)
        val deleteBtn: ImageView = view.findViewById(R.id.imageView2)
        val crownIcon: ImageView = view.findViewById(R.id.Crown)
        val star1: ImageView = view.findViewById(R.id.star1)
        val star2: ImageView = view.findViewById(R.id.star2)
        val star3: ImageView = view.findViewById(R.id.star3)
        val star4: ImageView = view.findViewById(R.id.star4)
        val star5: ImageView = view.findViewById(R.id.star5)
        val ratingTV: TextView = view.findViewById(R.id.ratingText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_player, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameText.text = items[position]
        val player = playerList[position]

        val ratings = playerRatingsList[position]
        val ratingList = ratings.split(",").mapNotNull { it.toIntOrNull() }

        if (ratingList.isNotEmpty()) {
            var ratingTotal = 0

            for (ratingTemp in ratingList) {
                ratingTotal += ratingTemp
            }

            val ratingMean = String.format("%.1f", ratingTotal.toDouble() / ratingList.size).toDouble()
            holder.ratingTV.text = ratingMean.toString()

            if (ratingMean > 0) {
                if (ratingMean >= 1) {
                    holder.star1.setImageResource(R.drawable.star_full)
                } else {
                    holder.star1.setImageResource(R.drawable.star_half)
                }
            }
            if (ratingMean > 1) {
                if (ratingMean >= 2) {
                    holder.star2.setImageResource(R.drawable.star_full)
                } else {
                    holder.star2.setImageResource(R.drawable.star_half)
                }
            }
            if (ratingMean > 2) {
                if (ratingMean >= 3) {
                    holder.star3.setImageResource(R.drawable.star_full)
                } else {
                    holder.star3.setImageResource(R.drawable.star_half)
                }
            }
            if (ratingMean > 3) {
                if (ratingMean >= 4) {
                    holder.star4.setImageResource(R.drawable.star_full)
                } else {
                    holder.star4.setImageResource(R.drawable.star_half)
                }
            }
            if (ratingMean > 4) {
                if (ratingMean >= 5) {
                    holder.star5.setImageResource(R.drawable.star_full)
                } else {
                    holder.star5.setImageResource(R.drawable.star_half)
                }
            }
        } else{
            holder.ratingTV.text = "-"
        }

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
            playerRatingsList.removeAt(position)
            val databaseConnection = FirebaseDBConnection()
            databaseConnection.updatePlayersInTeam(teamId, playerList)
            notifyItemRemoved(position)
        }
    }

    override fun getItemCount(): Int = items.size
}

