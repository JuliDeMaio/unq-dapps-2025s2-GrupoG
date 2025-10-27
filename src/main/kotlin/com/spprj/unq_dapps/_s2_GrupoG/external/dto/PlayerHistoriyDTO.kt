package com.spprj.unq_dapps._s2_GrupoG.external.dto

data class PlayerHistoryDTO(
    val appearances: Int,
    val goals: Int,
    val assists: Int,
    val yellowCards: Int,
    val redCards: Int,
    val minutesPlayed: Int,
    val averageRating: Double?
)