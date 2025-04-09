package com.astradevelop.playconnect

import com.google.firebase.Timestamp

class Match(
    val id: String,
    val date: Timestamp,
    val name: String,
    val description: String,
    val maxPlayers: String,
    val place: String,
    val players: List<Any?>,
    val sport: Long,
    val type: Long
) {
}