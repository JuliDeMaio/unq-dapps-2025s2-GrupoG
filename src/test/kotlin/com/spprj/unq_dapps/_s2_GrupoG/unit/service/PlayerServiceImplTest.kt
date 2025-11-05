package com.spprj.unq_dapps._s2_GrupoG.unit.service

import com.spprj.unq_dapps._s2_GrupoG.external.dto.PlayerHistoryDTO
import com.spprj.unq_dapps._s2_GrupoG.external.whoscored.WhoScoredScraper
import com.spprj.unq_dapps._s2_GrupoG.model.Player
import com.spprj.unq_dapps._s2_GrupoG.repositories.PlayerRepository
import com.spprj.unq_dapps._s2_GrupoG.service.impl.PlayerServiceImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull

class PlayerServiceImplTest {

    private lateinit var whoScoredScraper: WhoScoredScraper
    private lateinit var playerRepository: PlayerRepository
    private lateinit var playerService: PlayerServiceImpl

    @BeforeEach
    fun setup() {
        whoScoredScraper = Mockito.mock(WhoScoredScraper::class.java)
        playerRepository = Mockito.mock(PlayerRepository::class.java)
        playerService = PlayerServiceImpl(whoScoredScraper, playerRepository)
    }

    @Test
    fun `01 - should do nothing when scraper returns empty list`() {
        val teamId = "10"
        Mockito.`when`(whoScoredScraper.getPlayersOfTeam(teamId)).thenReturn(emptyList())

        playerService.populateDataBaseFromScrapperService(teamId)

        verify(playerRepository, never()).deleteAll(any())
        verify(playerRepository, never()).saveAll(any<List<Player>>())
    }

    @Test
    fun `02 - should replace old players and save new ones`() {
        val teamId = "15"
        val oldPlayers = listOf(Player(1L, teamId, "Old", 5, 1, 0, 6.0))
        val newPlayers = listOf(
            Player(null, teamId, "Messi", 10, 8, 5, 9.2),
            Player(null, teamId, "Di María", 9, 3, 4, 8.1)
        )

        Mockito.`when`(whoScoredScraper.getPlayersOfTeam(teamId)).thenReturn(newPlayers)
        Mockito.`when`(playerRepository.findByTeamId(teamId)).thenReturn(oldPlayers)
        Mockito.`when`(playerRepository.saveAll(any<List<Player>>())).thenReturn(newPlayers)

        playerService.populateDataBaseFromScrapperService(teamId)

        verify(playerRepository).deleteAll(oldPlayers)
        verify(playerRepository).saveAll(any<List<Player>>())
    }

    @Test
    fun `03 - should return players from DB`() {
        val teamId = "20"
        val players = listOf(Player(1L, teamId, "Messi", 10, 8, 5, 9.2))
        Mockito.`when`(playerRepository.findByTeamId(teamId)).thenReturn(players)

        val result = playerService.getPlayersFromDb(teamId)

        assertEquals(1, result.size)
        assertEquals("Messi", result.first().name)
    }

    @Test
    fun `04 - should get player history from scraper`() {
        val playerId = "123"
        val playerName = "messi"
        val history = PlayerHistoryDTO(
            appearances = 10,
            goals = 8,
            assists = 5,
            yellowCards = 1,
            redCards = 0,
            minutesPlayed = 900,
            averageRating = 8.9
        )

        Mockito.`when`(whoScoredScraper.getPlayerHistory(eq(playerId), eq(playerName))).thenReturn(history)

        val result = playerService.getPlayerHistory(playerId, playerName)

        assertNotNull(result)
        assertEquals(8, result?.goals)
        assertEquals(8.9, result?.averageRating)
    }

}
