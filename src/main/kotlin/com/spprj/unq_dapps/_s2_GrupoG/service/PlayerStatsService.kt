package com.spprj.unq_dapps._s2_GrupoG.service

import com.spprj.unq_dapps._s2_GrupoG.external.footballdata.FootballDataService
import com.spprj.unq_dapps._s2_GrupoG.external.whoscored.WhoScoredStatsProvider
import com.spprj.unq_dapps._s2_GrupoG.model.Player
import org.springframework.stereotype.Service

@Service
class PlayerStatsService(
    private val footballDataService: FootballDataService,
    private val whoScored: WhoScoredStatsProvider
) {
    fun playersOfTeamWithStats(teamId: Long): List<Player> {
        val squad = footballDataService.playersOfTeam(teamId)

        return squad.map { squadPlayer ->
            val stats = whoScored.getStats("Manchester City", squadPlayer.name) // ⚠️ por ahora teamName hardcodeado
            squadPlayer.copy(
                matchesPlayed = stats?.matchesPlayed ?: squadPlayer.matchesPlayed,
                goals = stats?.goals ?: squadPlayer.goals,
                assists = stats?.assists ?: squadPlayer.assists,
                rating = stats?.rating ?: squadPlayer.rating
            )
        }
    }
}