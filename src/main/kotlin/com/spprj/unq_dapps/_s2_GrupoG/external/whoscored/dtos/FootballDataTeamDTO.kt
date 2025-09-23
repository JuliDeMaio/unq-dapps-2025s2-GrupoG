package com.spprj.unq_dapps._s2_GrupoG.external.whoscored.dtos

data class FootballDataTeamResponse(
    val id: Long,
    val name: String,
    val squad: List<FootballDataPlayer> = emptyList()
)

data class FootballDataPlayer(
    val id: Long,
    val name: String,
    val position: String? = null,
    val nationality: String? = null,
    val shirtNumber: Int? = null
)