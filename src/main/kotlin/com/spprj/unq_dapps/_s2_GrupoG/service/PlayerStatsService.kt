package com.spprj.unq_dapps._s2_GrupoG.service

import com.spprj.unq_dapps._s2_GrupoG.external.footballdata.FootballDataService
import com.spprj.unq_dapps._s2_GrupoG.external.whoscored.WhoScoredScraper
import com.spprj.unq_dapps._s2_GrupoG.external.whoscored.WhoScoredStatsProvider
import com.spprj.unq_dapps._s2_GrupoG.model.Player
import org.springframework.stereotype.Service

@Service
class PlayerStatsService(
    private val footballDataService: FootballDataService,
    private val whoScored: WhoScoredScraper
) {
    fun playersOfTeamWithStats(teamId: Long): List<Player> {
        val squad = footballDataService.playersOfTeam(teamId)

        // ⚠️ Mapeo: cada equipo en football-data tiene que tener un teamId que coincida con WhoScored
        val whoScoredTeamId = when (teamId) {
            65L -> "167" // Manchester City (ejemplo)
            64L -> "26"  // Liverpool (ejemplo)
            else -> error("TeamId $teamId no mapeado a WhoScored")
        }

        val statsMap = whoScored.getAllPlayersStatsFromTeamPage(whoScoredTeamId)
        println("✅ Stats obtenidas: ${statsMap.keys}")

        return squad.map { p ->
            val stats = statsMap[p.name]
            p.copy(
                matchesPlayed = stats?.matchesPlayed ?: p.matchesPlayed,
                goals = stats?.goals ?: p.goals,
                assists = stats?.assists ?: p.assists,
                rating = stats?.rating ?: p.rating
            )
        }
    }
}
