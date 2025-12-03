package com.spprj.unq_dapps._s2_GrupoG.unit.service

import com.spprj.unq_dapps._s2_GrupoG.external.whoscored.WhoScoredScraper
import com.spprj.unq_dapps._s2_GrupoG.model.Player
import com.spprj.unq_dapps._s2_GrupoG.model.Team
import com.spprj.unq_dapps._s2_GrupoG.repositories.PlayerRepository
import com.spprj.unq_dapps._s2_GrupoG.repositories.TeamRepository
import com.spprj.unq_dapps._s2_GrupoG.service.impl.TeamServiceImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.util.*

class TeamServiceImplTest {

    private lateinit var scraper: WhoScoredScraper
    private lateinit var teamRepository: TeamRepository
    private lateinit var playerRepository: PlayerRepository
    private lateinit var service: TeamServiceImpl

    @BeforeEach
    fun setup() {
        scraper = Mockito.mock(WhoScoredScraper::class.java)
        teamRepository = Mockito.mock(TeamRepository::class.java)
        playerRepository = Mockito.mock(PlayerRepository::class.java)

        service = TeamServiceImpl(scraper, teamRepository, playerRepository)
    }

    // ============================================================
    // 01 - playersOfTeam
    // ============================================================

    @Test
    fun `01 - should delegate playersOfTeam to scraper`() {
        val teamId = "10"
        val players = listOf(
            Player(1, teamId, "A", 5, 1, 0, 7.5, 0, 0, 300, "p1")
        )

        `when`(scraper.getPlayersOfTeam(teamId)).thenReturn(players)

        val result = service.playersOfTeam(teamId)

        assertEquals(1, result.size)
        assertEquals("A", result[0].name)
        Mockito.verify(scraper).getPlayersOfTeam(teamId)
    }

    // ============================================================
    // 02 - getTeamById
    // ============================================================

    @Test
    fun `02 - should return team with computed average rating`() {
        val teamId = "20"
        val baseTeam = Team(teamId, "Barcelona", 0.0)

        val players = listOf(
            Player(1, teamId, "Messi", 10, 5, 3, 9.0, 0, 0, 800, "p1"),
            Player(2, teamId, "Xavi", 10, 1, 5, 7.0, 0, 0, 700, "p2")
        )

        `when`(teamRepository.findById(teamId)).thenReturn(Optional.of(baseTeam))
        `when`(scraper.getPlayersOfTeam(teamId)).thenReturn(players)

        val result = service.getTeamById(teamId)

        assertEquals("Barcelona", result.name)
        assertEquals(8.0, result.rating!!, 0.001)
    }

    @Test
    fun `03 - should return 0 rating when all players have null rating`() {
        val teamId = "30"
        val baseTeam = Team(teamId, "River", 0.0)

        val players = listOf(
            Player(1, teamId, "A", 10, 0, 0, null, 0, 0, 600, "1"),
            Player(2, teamId, "B", 10, 0, 0, null, 0, 0, 500, "2")
        )

        `when`(teamRepository.findById(teamId)).thenReturn(Optional.of(baseTeam))
        `when`(scraper.getPlayersOfTeam(teamId)).thenReturn(players)

        val result = service.getTeamById(teamId)

        assertEquals(0.0, result.rating)
    }

    @Test
    fun `04 - should throw when team not found`() {
        val teamId = "999"

        `when`(teamRepository.findById(teamId)).thenReturn(Optional.empty())

        val ex = assertThrows(IllegalArgumentException::class.java) {
            service.getTeamById(teamId)
        }

        assertTrue(ex.message!!.contains("No existe el equipo"))
    }

    // ============================================================
    // 03 - getTeamMetrics
    // ============================================================

    @Test
    fun `05 - should return empty map when no players`() {
        val teamId = "50"

        `when`(playerRepository.findByTeamId(teamId)).thenReturn(emptyList())

        val result = service.getTeamMetrics(teamId)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `06 - should compute metrics correctly`() {
        val teamId = "60"

        val players = listOf(
            Player(1, teamId, "A", 10, 4, 2, 8.0, 0, 0, 800, "1"),
            Player(2, teamId, "B", 5, 1, 1, 6.0, 0, 0, 500, "2")
        )

        `when`(playerRepository.findByTeamId(teamId)).thenReturn(players)

        val result = service.getTeamMetrics(teamId)

        assertEquals(15, players.sumOf { it.matchesPlayed })
        assertEquals(5.0 / 15.0, result["goalsPerMatch"])
        assertEquals(3.0 / 15.0, result["assistsPerMatch"])
        assertEquals(7.0, result["averageRating"])
    }

    @Test
    fun `07 - should handle matches = 0 safely`() {
        val teamId = "70"

        val players = listOf(
            Player(1, teamId, "A", 0, 0, 0, 8.0, 0, 0, 200, "1"),
            Player(2, teamId, "B", 0, 0, 0, 6.0, 0, 0, 150, "2")
        )

        `when`(playerRepository.findByTeamId(teamId)).thenReturn(players)

        val result = service.getTeamMetrics(teamId)

        assertEquals(0.0, result["goalsPerMatch"])
        assertEquals(0.0, result["assistsPerMatch"])
        assertEquals(7.0, result["averageRating"])
    }
}
