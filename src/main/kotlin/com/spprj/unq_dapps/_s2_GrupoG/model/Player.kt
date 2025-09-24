package com.spprj.unq_dapps._s2_GrupoG.model

data class Player(
    val name: String,
    val matchesPlayed: Int,
    val goals: Int,
    val assists: Int,
    val rating: Double?
)