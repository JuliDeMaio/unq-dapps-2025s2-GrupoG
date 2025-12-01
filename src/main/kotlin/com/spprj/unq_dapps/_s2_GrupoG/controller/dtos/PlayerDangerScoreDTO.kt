package com.spprj.unq_dapps._s2_GrupoG.controller.dtos

data class PlayerDangerScoreDTO(
    val playerName: String,
    val dangerScore: Double,
    val yellowCards: Int,
    val redCards: Int,
    val minutesPlayed: Int
)
