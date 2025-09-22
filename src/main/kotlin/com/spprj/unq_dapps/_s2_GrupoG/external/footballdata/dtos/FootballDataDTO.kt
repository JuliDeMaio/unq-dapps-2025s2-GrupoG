package com.spprj.unq_dapps._s2_GrupoG.external.footballdata.dtos

data class FootballDataTeamResponse(
    val id: Long,
    val name: String,
    val squad: List<FootballDataPlayer>
)

data class FootballDataPlayer(
    val id: Long,
    val name: String,
    val position: String?,
    val stats: FootballDataStats? = null
)

data class FootballDataStats(
    val matchesOnPitch: Int?,
    val goals: Int?,
    val assists: Int?
)