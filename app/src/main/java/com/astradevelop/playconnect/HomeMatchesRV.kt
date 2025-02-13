package com.astradevelop.playconnect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeMatchesRV(private val items: ArrayList<ArrayList<String>>) : RecyclerView.Adapter<HomeMatchesRV.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val sportText: TextView = view.findViewById(R.id.sportText)
        val dateText: TextView = view.findViewById(R.id.dateText)
        val locationText: TextView = view.findViewById(R.id.locationText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_match, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val matchId = items[position]
        holder.sportText.text = items[position][1]
        holder.dateText.text = items[position][2]
        holder.locationText.text = items[position][3]
    }

    override fun getItemCount(): Int = items.size
}

