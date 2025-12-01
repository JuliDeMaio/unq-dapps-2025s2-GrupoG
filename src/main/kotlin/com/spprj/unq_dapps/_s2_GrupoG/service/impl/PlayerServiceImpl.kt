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

        val basePlayers = whoScoredScraper.getPlayersOfTeam(teamId)

        if (basePlayers.isEmpty()) {
            println("⚠️ No players found for team $teamId")
            return
        }

        // Borrar jugadores anteriores
        playerRepository.deleteAll(playerRepository.findByTeamId(teamId))

        // Guardar jugadores scrapeados directamente
        playerRepository.saveAll(basePlayers)

        println("✅ Saved ${basePlayers.size} players for team $teamId")
    }

    fun getPlayersFromDb(teamId: String): List<Player> {
        return playerRepository.findByTeamId(teamId)
    }

    fun getPlayerHistory(playerId: String, playerName: String): PlayerHistoryDTO? {
        return whoScoredScraper.getPlayerHistory(playerId, playerName)
    }
}
