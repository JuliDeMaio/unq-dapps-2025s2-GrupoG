package com.spprj.unq_dapps._s2_GrupoG.external.whoscored

data class PlayerStatsData(
    val matchesPlayed: Int = 0,
    val goals: Int = 0,
    val assists: Int = 0,
    val rating: Double? = null
)

interface WhoScoredStatsProvider {
    fun getStats(teamName: String, playerName: String): PlayerStatsData?
}