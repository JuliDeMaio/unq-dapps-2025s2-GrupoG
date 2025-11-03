package com.spprj.unq_dapps._s2_GrupoG.service.impl

import com.spprj.unq_dapps._s2_GrupoG.external.whoscored.WhoScoredScraper
import com.spprj.unq_dapps._s2_GrupoG.model.Player
import com.spprj.unq_dapps._s2_GrupoG.model.Team
import com.spprj.unq_dapps._s2_GrupoG.repositories.TeamRepository
import com.spprj.unq_dapps._s2_GrupoG.service.TeamService
import org.springframework.stereotype.Service

@Service
class TeamServiceImpl(
    private val whoScoredScraper: WhoScoredScraper,
    private val teamRepository: TeamRepository
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
}