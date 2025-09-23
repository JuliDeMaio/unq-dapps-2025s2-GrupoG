package com.spprj.unq_dapps._s2_GrupoG.external.footballdata

import com.spprj.unq_dapps._s2_GrupoG.model.Player
import com.spprj.unq_dapps._s2_GrupoG.model.PlayerStats
import org.springframework.stereotype.Service

@Service
class PlayerStatsAggregatorService(
    private val footballDataService: FootballDataService
) {
    fun playersWithStats(teamId: Long, limit: Int = 10): List<Player> {
        val squad = footballDataService.playersOfTeam(teamId)
        val stats = mutableMapOf<String, PlayerStats>()

        val matchIds = footballDataService.getRecentMatches(teamId, limit)
        matchIds.forEach { matchId ->
            val match = footballDataService.getMatch(matchId)

            // 1. goles y asistencias desde eventos
            match.events.forEach { e ->
                val playerName = e.player?.name
                if (!playerName.isNullOrBlank()) {
                    stats.computeIfAbsent(playerName) { PlayerStats(playerName) }
                        .apply {
                            when (e.type?.uppercase()) {
                                "GOAL" -> goals++
                            }
                        }
                }

                val assistName = e.assist?.name
                if (!assistName.isNullOrBlank()) {
                    stats.computeIfAbsent(assistName) { PlayerStats(assistName) }
                        .apply { assists++ }
                }
            }

            // 2. partidos jugados desde lineup (titulares + suplentes)
            match.lineups.forEach { lineup ->
                (lineup.startXI + lineup.substitutes).forEach { lp ->
                    val name = lp.name ?: return@forEach
                    stats.computeIfAbsent(name) { PlayerStats(name) }
                        .apply { matchesPlayed++ }
                }
            }
        }

        // 3. fusionar stats con squad
        return squad.map { p ->
            val s = stats[p.name]
            p.copy(
                matchesPlayed = s?.matchesPlayed ?: 0,
                goals = s?.goals ?: 0,
                assists = s?.assists ?: 0,
                rating = null
            )
        }
    }
}
