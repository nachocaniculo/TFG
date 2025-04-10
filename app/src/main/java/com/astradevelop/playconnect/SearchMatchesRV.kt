package com.astradevelop.playconnect

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

class SearchMatchesRV(
    private val items: ArrayList<Match>,
    private val user: String,
    private val searchActivity: SearchActivity,
    private val sport: Int
) : RecyclerView.Adapter<SearchMatchesRV.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val sportText: TextView = view.findViewById(R.id.sportText)
        val dateText: TextView = view.findViewById(R.id.dateText)
        val locationText: TextView = view.findViewById(R.id.locationText)
        val joinButton: LinearLayout = view.findViewById(R.id.joinButton)
        val sportIcon: ImageView = view.findViewById(R.id.sportIcon)
        val sportButton: LinearLayout = view.findViewById(R.id.sportButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_match_join, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val timestamp: Timestamp = items[position].date

        val date = timestamp.toDate()

        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val formattedDate = dateFormat.format(date)

        holder.dateText.text = formattedDate

        holder.locationText.text = items[position].place
        holder.joinButton.setOnClickListener {
            if (items[position].type == 1.toLong()) {
                FirebaseDBConnection().updateTeam(items[position].id, user)
                items.removeAt(position)
                notifyItemRemoved(position)
            } else {
                val intent = Intent(searchActivity, MatchActivity::class.java)
                intent.putExtra("MatchID", items[position].id)
                searchActivity.startActivity(intent)
            }
        }
        when (sport){
            0 -> {holder.sportIcon.setImageResource(R.drawable.padelicon)
                holder.sportText.text = "Padel"}
            1 -> {holder.sportIcon.setImageResource(R.drawable.tennisicon)
                holder.sportText.text = "Tennis"}
            2 -> {holder.sportIcon.setImageResource(R.drawable.basketicon)
                holder.sportText.text = "Basketball"}
            3 -> {holder.sportIcon.setImageResource(R.drawable.footballicon)
                holder.sportText.text = "Football"}
        }
        holder.sportButton.setOnClickListener {
            val intent = Intent(searchActivity, MatchActivity::class.java)
            intent.putExtra("MatchID", items[position].id)
            searchActivity.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size
}

