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

        val basePlayers: List<Player> = whoScoredScraper.getPlayersOfTeam(teamId)

        if (basePlayers.isEmpty()) {
            return
        }

        // Limpio jugadores previos del equipo para evitar duplicados
        val deleted = playerRepository.findByTeamId(teamId)
        playerRepository.deleteAll(deleted)

        val playersToSave = mutableListOf<Player>()

        for (p in basePlayers) {

            val playerNameSlug = p.name.lowercase()
                .replace(" ", "-")
                .replace(".", "")
                .replace("'", "")

            // Atención: no siempre coincide el id de WhoScored con el jugador.
            // Si no tenés el ID del jugador, NO se puede scrapear la historia.
            // Voy a intentar usar el name slug (tu endpoint lo usa así).
            val history = whoScoredScraper.getPlayerHistory(p.id?.toString() ?: "", playerNameSlug)

            val minutes = history?.minutesPlayed ?: 0
            val yellow = history?.yellowCards ?: 0
            val red = history?.redCards ?: 0

            val mergedPlayer = Player(
                id = null,
                teamId = teamId,
                name = p.name,
                matchesPlayed = p.matchesPlayed,
                goals = p.goals,
                assists = p.assists,
                rating = p.rating,
                minutesPlayed = minutes,
                yellowCards = yellow,
                redCards = red
            )

            playersToSave.add(mergedPlayer)
        }

        playerRepository.saveAll(playersToSave)
    }

    fun getPlayersFromDb(teamId: String): List<Player> {
        return playerRepository.findByTeamId(teamId)
    }

    fun getPlayerHistory(playerId: String, playerName: String): PlayerHistoryDTO? {
        return whoScoredScraper.getPlayerHistory(playerId, playerName)
    }
}
