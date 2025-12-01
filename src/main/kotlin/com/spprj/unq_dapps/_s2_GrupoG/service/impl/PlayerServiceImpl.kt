package com.spprj.unq_dapps._s2_GrupoG.service.impl

import com.spprj.unq_dapps._s2_GrupoG.external.dto.PlayerHistoryDTO
import com.spprj.unq_dapps._s2_GrupoG.external.whoscored.WhoScoredScraper
import com.spprj.unq_dapps._s2_GrupoG.model.Player
import com.spprj.unq_dapps._s2_GrupoG.repositories.PlayerRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PlayerServiceImpl(
    private val whoScoredScraper: WhoScoredScraper,
    private val playerRepository: PlayerRepository
) {

    @Transactional
    fun populateDataBaseFromScrapperService(teamId: String) {
        val players: List<Player> = whoScoredScraper.getPlayersOfTeam(teamId)

        if (players.isEmpty()) {
            return
        }

        val deleted = playerRepository.findByTeamId(teamId)
        playerRepository.deleteAll(deleted)

        playerRepository.saveAll(
            players.map { p ->
                Player(
                    id = null,
                    teamId = teamId,
                    name = p.name,
                    matchesPlayed = p.matchesPlayed,
                    goals = p.goals,
                    assists = p.assists,
                    rating = p.rating,
                    yellowCards = p.yellowCards,
                    redCards = p.redCards,
                    minutesPlayed = p.minutesPlayed,
                    whoScoredId = p.whoScoredId
                )
            }
        )
    }


    fun getPlayersFromDb(teamId: String): List<Player> {
        return playerRepository.findByTeamId(teamId)
    }

    fun getPlayerHistory(playerId: String, playerName: String): PlayerHistoryDTO? {
        return whoScoredScraper.getPlayerHistory(playerId, playerName)
    }
}
