package com.astradevelop.playconnect

import com.google.firebase.Timestamp

class Tournament(
    val id: String,
    val name: String,
    val location: String,
    val sport: Long,
    val teams: List<Any?>,
    val type: Long,
    val teamMaxNum: Long,
    val startDate: Timestamp,
    val admin: String
) {
}