package com.astradevelop.playconnect


data class EquipoSlot(
    val name: String?,
    val previousMatch: String?
)
data class TournamentMatch(
    val team1: EquipoSlot,
    val team2: EquipoSlot,
    val status: String,
    val round: Long
) {
}