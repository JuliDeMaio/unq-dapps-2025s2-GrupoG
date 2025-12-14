package com.spprj.unq_dapps._s2_GrupoG.service.impl

import com.spprj.unq_dapps._s2_GrupoG.external.dto.PlayerHistoryDTO
import com.spprj.unq_dapps._s2_GrupoG.external.whoscored.WhoScoredScraper
import com.spprj.unq_dapps._s2_GrupoG.model.Player
import com.spprj.unq_dapps._s2_GrupoG.repositories.PlayerRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PlayerServiceImpl(
    private val whoScoredScraper: WhoScoredScraper,
    private val playerRepository: PlayerRepository
) {

    private val logger = LoggerFactory.getLogger(PlayerServiceImpl::class.java)

    @Transactional
    fun populateDataBaseFromScrapperService(teamId: String) {

        val basePlayers = whoScoredScraper.getPlayersOfTeam(teamId)

        if (basePlayers.isEmpty()) {
            logger.warn("No players found for team {}", teamId)
            return
        }

        playerRepository.deleteAll(playerRepository.findByTeamId(teamId))
        playerRepository.saveAll(basePlayers)

        logger.info("Saved {} players for team {}", basePlayers.size, teamId)
    }

    fun getPlayersFromDb(teamId: String): List<Player> {
        return playerRepository.findByTeamId(teamId)
    }

    fun getPlayerHistory(playerId: String, playerName: String): PlayerHistoryDTO? {
        return whoScoredScraper.getPlayerHistory(playerId, playerName)
    }
}