package com.astradevelop.playconnect

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HomeMatchesRV(
    private val items: ArrayList<ArrayList<String>>,
    private val homeActivity: HomeActivity,
    private val user: String
) : RecyclerView.Adapter<HomeMatchesRV.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val sportText: TextView = view.findViewById(R.id.sportText)
        val dateText: TextView = view.findViewById(R.id.dateText)
        val locationText: TextView = view.findViewById(R.id.locationText)
        val editButton: LinearLayout = view.findViewById(R.id.editButton)
        val editText: TextView = view.findViewById(R.id.editText)
        val leaveButton: LinearLayout = view.findViewById(R.id.leaveButton)
        val leaveText: TextView = view.findViewById(R.id.leaveText)
    }


    private val firebaseDBConnection = FirebaseDBConnection()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_match, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val matchId = items[position][0]
        val team1 = items[position][4]
        holder.sportText.text = items[position][1]
        holder.dateText.text = items[position][2]
        holder.locationText.text = items[position][3]
        if (team1 != user){
            holder.editButton.visibility = View.GONE
            holder.editText.visibility = View.GONE
            holder.leaveButton.visibility = View.VISIBLE
            holder.leaveText.visibility = View.VISIBLE
        } else {
            holder.editButton.visibility = View.VISIBLE
            holder.editText.visibility = View.VISIBLE
            holder.leaveButton.visibility = View.GONE
            holder.leaveText.visibility = View.GONE
        }
        holder.editButton.setOnClickListener {
            val intent = Intent(homeActivity, UpdateActivity::class.java)
            intent.putExtra("id", matchId)
            intent.putExtra("date", items[position][2])
            intent.putExtra("location", items[position][3])
            homeActivity.startActivity(intent)
        }
        holder.leaveButton.setOnClickListener {
            showDeleteConfirmationDialog(homeActivity, matchId)
        }
    }

    override fun getItemCount(): Int = items.size

    private fun showDeleteConfirmationDialog(context: Context, matchId: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Confirm Deletion")
        builder.setMessage("Are you sure you want to leave this match?")

        builder.setPositiveButton("Leave") { _, _ ->
            firebaseDBConnection.updateTeam2(matchId, "")
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

