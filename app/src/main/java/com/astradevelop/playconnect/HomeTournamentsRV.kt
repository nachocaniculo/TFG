package com.astradevelop.playconnect

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
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeTournamentsRV(
    private val items: ArrayList<Tournament>,
    private val homeActivity: HomeActivity,
    private val user: String
) : RecyclerView.Adapter<HomeTournamentsRV.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val sportText: TextView = view.findViewById(R.id.sportText)
        val dateText: TextView = view.findViewById(R.id.dateText)
        val locationText: TextView = view.findViewById(R.id.locationText)
        val sportIcon: ImageView = view.findViewById(R.id.sportIcon)
        val sportButton: LinearLayout = view.findViewById(R.id.sportButton)
    }


    private val firebaseDBConnection = FirebaseDBConnection()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_tournament, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val matchId = items[position].id
        val matchName = items[position].name
        val sport = items[position].sport.toString()

        val timestamp: Timestamp = items[position].startDate

        val date = timestamp.toDate()

        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val formattedDate = dateFormat.format(date)

        holder.dateText.text = formattedDate

        holder.sportText.text = matchName

        holder.locationText.text = items[position].location
        holder.sportButton.setOnClickListener {
            val intent = Intent(homeActivity, TournamentActivity::class.java)
            intent.putExtra("tournament", matchId)
            homeActivity.startActivity(intent)
        }
        when (sport){
            "0" -> holder.sportIcon.setImageResource(R.drawable.padelicon)
            "1" -> holder.sportIcon.setImageResource(R.drawable.tennisicon)
            "2" -> holder.sportIcon.setImageResource(R.drawable.basketicon)
            "3" -> holder.sportIcon.setImageResource(R.drawable.footballicon)
        }
    }

    override fun getItemCount(): Int = items.size

    private fun showDeleteConfirmationDialog(context: Context, matchId: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Confirm Deletion")
        builder.setMessage("Are you sure you want to leave this match?")

        builder.setPositiveButton("Leave") { _, _ ->
            firebaseDBConnection.updateTeam2(matchId, user)
            val intent = Intent(context, HomeActivity::class.java)
            context.startActivity(intent)
            (context as? Activity)?.finish()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
}

