package com.astradevelop.playconnect

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.api.Distribution.BucketOptions.Linear

class TournamentPlayersRV(
    private val items: MutableList<String>
) : RecyclerView.Adapter<TournamentPlayersRV.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.sportText)
        val deleteBtn: ImageView = view.findViewById(R.id.imageView2)
        val profilePic: ImageView = view.findViewById(R.id.profilePic)
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
    }

    override fun getItemCount(): Int = items.size
}

