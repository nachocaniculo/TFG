package com.astradevelop.playconnect

import android.annotation.SuppressLint
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

class SportsSearchRV(
    private val searchActivity: SearchActivity
) : RecyclerView.Adapter<SportsSearchRV.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val sportText: TextView = view.findViewById(R.id.sportText)
        val sportImage: ImageView = view.findViewById(R.id.padelIcon)
        val sportButton: LinearLayout = view.findViewById(R.id.sportButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_sport, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (position){
            0 -> {holder.sportText.text = "Padel"
                holder.sportImage.setImageResource(R.drawable.padelicon)}
            1 -> {holder.sportText.text = "Tennis"
                holder.sportImage.setImageResource(R.drawable.tennisicon)}
            2 -> {holder.sportText.text = "Basketball"
                holder.sportImage.setImageResource(R.drawable.basketicon)}
            3 -> {holder.sportText.text = "Football"
                holder.sportImage.setImageResource(R.drawable.footballicon)}
        }
        holder.sportButton.setOnClickListener {
            searchActivity.setSport(position)
        }
    }

    override fun getItemCount(): Int = 4
}

