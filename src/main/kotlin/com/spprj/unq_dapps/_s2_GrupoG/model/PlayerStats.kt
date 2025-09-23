package com.spprj.unq_dapps._s2_GrupoG.model

data class PlayerStats(
    val name: String,
    var matchesPlayed: Int = 0,
    var goals: Int = 0,
    var assists: Int = 0
)
