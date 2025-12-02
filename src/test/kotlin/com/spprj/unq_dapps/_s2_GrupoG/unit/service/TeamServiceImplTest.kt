package com.spprj.unq_dapps._s2_GrupoG.unit.service

import com.spprj.unq_dapps._s2_GrupoG.external.whoscored.WhoScoredScraper
import com.spprj.unq_dapps._s2_GrupoG.model.Player
import com.spprj.unq_dapps._s2_GrupoG.model.Team
import com.spprj.unq_dapps._s2_GrupoG.repositories.TeamRepository
import com.spprj.unq_dapps._s2_GrupoG.service.impl.TeamServiceImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.util.*

class TeamServiceImplTest {

    private lateinit var whoScoredScraper: WhoScoredScraper
    private lateinit var teamRepository: TeamRepository
    private lateinit var teamService: TeamServiceImpl

    @BeforeEach
    fun setup() {
        whoScoredScraper = Mockito.mock(WhoScoredScraper::class.java)
        teamRepository = Mockito.mock(TeamRepository::class.java)
        teamService = TeamServiceImpl(whoScoredScraper, teamRepository)
    }

    @Test
    fun `01 - should return players of team`() {
        val fakeTeamId = "10"
        val fakePlayers = listOf(
            Player(
                id = 1L,
                teamId = fakeTeamId,
                name = "Messi",
                matchesPlayed = 10,
                goals = 8,
                assists = 5,
                rating = 9.3,
                yellowCards = 2,
                redCards = 0,
                minutesPlayed = 900,
                whoScoredId = "m10"
            ),
            Player(
                id = 2L,
                teamId = fakeTeamId,
                name = "Di María",
                matchesPlayed = 8,
                goals = 3,
                assists = 4,
                rating = 8.1,
                yellowCards = 1,
                redCards = 0,
                minutesPlayed = 700,
                whoScoredId = "dm10"
            )
        )

        `when`(whoScoredScraper.getPlayersOfTeam(fakeTeamId)).thenReturn(fakePlayers)

        val result = teamService.playersOfTeam(fakeTeamId)

        assertEquals(2, result.size)
        assertEquals("Messi", result.first().name)
        Mockito.verify(whoScoredScraper).getPlayersOfTeam(fakeTeamId)
    }

    @Test
    fun `02 - should calculate average rating correctly`() {
        val fakeTeamId = "20"
        val fakeTeam = Team(fakeTeamId, "Barcelona", 0.0)

        val fakePlayers = listOf(
            Player(
                id = 1L,
                teamId = fakeTeamId,
                name = "Messi",
                matchesPlayed = 10,
                goals = 8,
                assists = 5,
                rating = 9.0,
                yellowCards = 0,
                redCards = 0,
                minutesPlayed = 1000,
                whoScoredId = "m20"
            ),
            Player(
                id = 2L,
                teamId = fakeTeamId,
                name = "Suárez",
                matchesPlayed = 10,
                goals = 5,
                assists = 3,
                rating = 8.0,
                yellowCards = 0,
                redCards = 0,
                minutesPlayed = 900,
                whoScoredId = "s20"
            )
        )

        `when`(teamRepository.findById(fakeTeamId)).thenReturn(Optional.of(fakeTeam))
        `when`(whoScoredScraper.getPlayersOfTeam(fakeTeamId)).thenReturn(fakePlayers)

        val result = teamService.getTeamById(fakeTeamId)

        assertEquals("Barcelona", result.name)
        assertEquals(8.5, result.rating ?: 0.0, 0.001)
    }

    @Test
    fun `03 - should return 0 rating when players have no rating`() {
        val fakeTeamId = "22"
        val fakeTeam = Team(fakeTeamId, "River", 0.0)

        val fakePlayers = listOf(
            Player(
                id = 1L,
                teamId = fakeTeamId,
                name = "Player A",
                matchesPlayed = 10,
                goals = 0,
                assists = 0,
                rating = null,
                yellowCards = 0,
                redCards = 0,
                minutesPlayed = 800,
                whoScoredId = "pa22"
            ),
            Player(
                id = 2L,
                teamId = fakeTeamId,
                name = "Player B",
                matchesPlayed = 10,
                goals = 0,
                assists = 0,
                rating = null,
                yellowCards = 0,
                redCards = 0,
                minutesPlayed = 700,
                whoScoredId = "pb22"
            )
        )

        `when`(teamRepository.findById(fakeTeamId)).thenReturn(Optional.of(fakeTeam))
        `when`(whoScoredScraper.getPlayersOfTeam(fakeTeamId)).thenReturn(fakePlayers)

        val result = teamService.getTeamById(fakeTeamId)

        assertEquals(0.0, result.rating ?: 0.0)
    }

    @Test
    fun `04 - should throw exception when team not found`() {
        val fakeTeamId = "99"
        `when`(teamRepository.findById(fakeTeamId)).thenReturn(Optional.empty())

        val exception = assertThrows(IllegalArgumentException::class.java) {
            teamService.getTeamById(fakeTeamId)
        }

        assertTrue(exception.message!!.contains("No existe el equipo con ID"))
    }
}
