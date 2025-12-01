package com.spprj.unq_dapps._s2_GrupoG.service.impl

import com.spprj.unq_dapps._s2_GrupoG.external.whoscored.WhoScoredScraper
import com.spprj.unq_dapps._s2_GrupoG.model.Player
import com.spprj.unq_dapps._s2_GrupoG.model.Team
import com.spprj.unq_dapps._s2_GrupoG.repositories.PlayerRepository
import com.spprj.unq_dapps._s2_GrupoG.repositories.TeamRepository
import com.spprj.unq_dapps._s2_GrupoG.service.TeamService
import org.springframework.stereotype.Service

@Service
class TeamServiceImpl(
    private val whoScoredScraper: WhoScoredScraper,
    private val teamRepository: TeamRepository,
    private val playerRepository: PlayerRepository
) : TeamService {

    override fun playersOfTeam(teamId: String): List<Player> {
        return whoScoredScraper.getPlayersOfTeam(teamId)
    }

    fun getTeamById(teamId: String): Team {
        val team = teamRepository.findById(teamId).orElseThrow {
            IllegalArgumentException("No existe el equipo con ID $teamId")
        }

        val players = whoScoredScraper.getPlayersOfTeam(teamId)
        val averageRating = players.mapNotNull { it.rating }.average().takeIf { !it.isNaN() } ?: 0.0

        return Team(
            id = team.id,
            name = team.name,
            rating = averageRating
        )
    }

    fun getTeamMetrics(teamId: String): Map<String, Double> {
        val players = playerRepository.findByTeamId(teamId)

        if (players.isEmpty()) return emptyMap()

        val matches = players.sumOf { it.matchesPlayed }
        val goals = players.sumOf { it.goals }
        val assists = players.sumOf { it.assists }
        val ratings = players.mapNotNull { it.rating }

        val averageRating = if (ratings.isEmpty()) 0.0 else ratings.average()
        val goalsPerMatch = if (matches == 0) 0.0 else goals.toDouble() / matches
        val assistsPerMatch = if (matches == 0) 0.0 else assists.toDouble() / matches

        return mapOf(
            "averageRating" to averageRating,
            "goalsPerMatch" to goalsPerMatch,
            "assistsPerMatch" to assistsPerMatch,
        )
    }
}
