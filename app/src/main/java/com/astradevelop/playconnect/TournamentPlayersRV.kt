package com.astradevelop.playconnect

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TournamentPlayersRV(
    private val items: MutableList<String>,
    private val playerRatingsList: MutableList<String>
) : RecyclerView.Adapter<TournamentPlayersRV.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.sportText)
        val deleteBtn: ImageView = view.findViewById(R.id.imageView2)
        val profilePic: ImageView = view.findViewById(R.id.profilePic)
        val star1: ImageView = view.findViewById(R.id.star1)
        val star2: ImageView = view.findViewById(R.id.star2)
        val star3: ImageView = view.findViewById(R.id.star3)
        val star4: ImageView = view.findViewById(R.id.star4)
        val star5: ImageView = view.findViewById(R.id.star5)
        val ratingTV: TextView = view.findViewById(R.id.ratingText)
        val starLL: LinearLayout = view.findViewById(R.id.starLL)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_player, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameText.text = items[position]
        holder.deleteBtn.visibility = View.GONE
        val ratings = playerRatingsList[position]
        println("Rating: {$ratings}")
        val ratingList = ratings.split(",").mapNotNull { it.toIntOrNull() }
        if (ratingList.isNotEmpty()) {

            if (ratingList[0] != 6) {
                var ratingTotal = 0

                for (ratingTemp in ratingList) {
                    ratingTotal += ratingTemp
                }

                val ratingMean =
                    String.format("%.1f", ratingTotal.toDouble() / ratingList.size).toDouble()
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
            } else {
                holder.ratingTV.visibility = View.GONE
                holder.starLL.visibility = View.GONE
            }
        } else {
            holder.ratingTV.visibility = View.GONE
            holder.starLL.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = items.size
}

