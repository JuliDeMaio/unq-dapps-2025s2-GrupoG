package com.spprj.unq_dapps._s2_GrupoG.service.impl

import com.spprj.unq_dapps._s2_GrupoG.controller.dtos.PlayerDangerScoreDTO
import com.spprj.unq_dapps._s2_GrupoG.controller.dtos.TeamMostDangerousPlayerDTO
import com.spprj.unq_dapps._s2_GrupoG.model.Player
import com.spprj.unq_dapps._s2_GrupoG.repositories.PlayerRepository
import com.spprj.unq_dapps._s2_GrupoG.repositories.TeamRepository
import org.springframework.stereotype.Service
import kotlin.math.min

@Service
class DangerScoreServiceImpl(
    private val playerRepository: PlayerRepository,
    private val teamRepository: TeamRepository
) {

    fun calculateDangerScore(whoScoredId: String, playerName: String): PlayerDangerScoreDTO? {
        val player = playerRepository.findByWhoScoredId(whoScoredId) ?: return null
        val dangerScore = calculateDangerForPlayer(player)

        return PlayerDangerScoreDTO(
            playerName = player.name,
            dangerScore = dangerScore,
            yellowCards = player.yellowCards,
            redCards = player.redCards,
            minutesPlayed = player.minutesPlayed
        )
    }


    fun getMostDangerousPlayer(teamId: String): TeamMostDangerousPlayerDTO? {
        val team = teamRepository.findById(teamId).orElse(null) ?: return null
        val players = playerRepository.findByTeamId(teamId)
        if (players.isEmpty()) return null

        val mostDangerous = players.maxByOrNull { calculateDangerForPlayer(it) } ?: return null
        val dangerScore = calculateDangerForPlayer(mostDangerous)

        return TeamMostDangerousPlayerDTO(
            teamName = team.name,
            mostDangerousPlayer = mostDangerous.name,
            dangerScore = dangerScore
        )
    }

    private fun calculateDangerForPlayer(player: Player): Double {
        val minutes = maxOf(player.minutesPlayed, 1)
        val yellowScore = player.yellowCards * 1.5
        val redScore = player.redCards * 6.0
        val minutesFactor = min(300.0 / minutes, 1.5)
        val rawScore = (yellowScore + redScore) * minutesFactor
        return min(rawScore, 10.0)
    }
}
