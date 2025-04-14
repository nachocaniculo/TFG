package com.astradevelop.playconnect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RatePlayersRV(
    private val items: List<String>,
    private val endMatchActivity: EndMatchActivity
) : RecyclerView.Adapter<RatePlayersRV.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.sportText)
        val star1: ImageView = view.findViewById(R.id.star1)
        val star2: ImageView = view.findViewById(R.id.star2)
        val star3: ImageView = view.findViewById(R.id.star3)
        val star4: ImageView = view.findViewById(R.id.star4)
        val star5: ImageView = view.findViewById(R.id.star5)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_rate, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameText.text = items[position]
        endMatchActivity.updateList(0,position)
        holder.star1.setOnClickListener {
            holder.star1.setImageResource(R.drawable.star_full)
            holder.star2.setImageResource(R.drawable.star_empty)
            holder.star3.setImageResource(R.drawable.star_empty)
            holder.star4.setImageResource(R.drawable.star_empty)
            holder.star5.setImageResource(R.drawable.star_empty)
            endMatchActivity.updateList(1,position)
        }
        holder.star2.setOnClickListener {
            holder.star1.setImageResource(R.drawable.star_full)
            holder.star2.setImageResource(R.drawable.star_full)
            holder.star3.setImageResource(R.drawable.star_empty)
            holder.star4.setImageResource(R.drawable.star_empty)
            holder.star5.setImageResource(R.drawable.star_empty)
            endMatchActivity.updateList(2,position)
        }
        holder.star3.setOnClickListener {
            holder.star1.setImageResource(R.drawable.star_full)
            holder.star2.setImageResource(R.drawable.star_full)
            holder.star3.setImageResource(R.drawable.star_full)
            holder.star4.setImageResource(R.drawable.star_empty)
            holder.star5.setImageResource(R.drawable.star_empty)
            endMatchActivity.updateList(3,position)
        }
        holder.star4.setOnClickListener {
            holder.star1.setImageResource(R.drawable.star_full)
            holder.star2.setImageResource(R.drawable.star_full)
            holder.star3.setImageResource(R.drawable.star_full)
            holder.star4.setImageResource(R.drawable.star_full)
            holder.star5.setImageResource(R.drawable.star_empty)
            endMatchActivity.updateList(4,position)
        }
        holder.star5.setOnClickListener {
            holder.star1.setImageResource(R.drawable.star_full)
            holder.star2.setImageResource(R.drawable.star_full)
            holder.star3.setImageResource(R.drawable.star_full)
            holder.star4.setImageResource(R.drawable.star_full)
            holder.star5.setImageResource(R.drawable.star_full)
            endMatchActivity.updateList(5,position)
        }
    }

    override fun getItemCount(): Int = items.size
}

