package com.spprj.unq_dapps._s2_GrupoG.unit.service

import com.spprj.unq_dapps._s2_GrupoG.controller.dtos.PlayerDangerScoreDTO
import com.spprj.unq_dapps._s2_GrupoG.model.Player
import com.spprj.unq_dapps._s2_GrupoG.repositories.PlayerRepository
import com.spprj.unq_dapps._s2_GrupoG.repositories.TeamRepository
import com.spprj.unq_dapps._s2_GrupoG.service.impl.DangerScoreServiceImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.util.*

class DangerScoreServiceImplTest {

    @Mock
    private lateinit var playerRepository: PlayerRepository

    @Mock
    private lateinit var teamRepository: TeamRepository

    private lateinit var service: DangerScoreServiceImpl

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        service = DangerScoreServiceImpl(playerRepository, teamRepository)
    }

    @Test
    fun `01 - should calculate danger score correctly`() {
        val player = Player(
            id = null,
            teamId = "10",
            name = "Danger Man",
            matchesPlayed = 20,
            goals = 5,
            assists = 3,
            rating = 7.0,
            yellowCards = 4,
            redCards = 1,
            minutesPlayed = 200,
            whoScoredId = "99"
        )

        `when`(playerRepository.findByWhoScoredId("99")).thenReturn(player)

        val result: PlayerDangerScoreDTO? = service.calculateDangerScore("99", "Danger Man")

        assertNotNull(result)
        assertEquals("Danger Man", result!!.playerName)

        // According to current formula, score must be capped at 10
        assertEquals(10.0, result.dangerScore, 0.0001)
        assertEquals(4, result.yellowCards)
        assertEquals(1, result.redCards)
        assertEquals(200, result.minutesPlayed)
    }

    @Test
    fun `02 - should return null if player not found`() {
        `when`(playerRepository.findByWhoScoredId("nope")).thenReturn(null)

        val result = service.calculateDangerScore("nope", "Ghost")

        assertNull(result)
    }

    @Test
    fun `03 - should return most dangerous player`() {
        val teamId = "33"

        val team = mock(com.spprj.unq_dapps._s2_GrupoG.model.Team::class.java)
        `when`(team.name).thenReturn("River Plate")
        `when`(teamRepository.findById(teamId)).thenReturn(Optional.of(team))

        val players = listOf(
            Player(null, teamId, "Jugador 1", 10, 1, 0, 6.5, 1, 0, 300, "p1"),
            Player(null, teamId, "Jugador 2", 10, 0, 0, 6.0, 3, 1, 200, "p2"), // most dangerous
            Player(null, teamId, "Jugador 3", 10, 0, 0, 6.0, 0, 0, 500, "p3")
        )

        `when`(playerRepository.findByTeamId(teamId)).thenReturn(players)

        val result = service.getMostDangerousPlayer(teamId)

        assertNotNull(result)
        assertEquals("River Plate", result!!.teamName)
        assertEquals("Jugador 2", result.mostDangerousPlayer)
        assertEquals(10.0, result.dangerScore, 0.0001) // capped
    }

    @Test
    fun `04 - should return null if team does not exist`() {
        `when`(teamRepository.findById("500")).thenReturn(Optional.empty())

        val result = service.getMostDangerousPlayer("500")

        assertNull(result)
    }

    @Test
    fun `05 - should return null when team has no players`() {
        val teamId = "44"
        val team = mock(com.spprj.unq_dapps._s2_GrupoG.model.Team::class.java)

        `when`(teamRepository.findById(teamId)).thenReturn(Optional.of(team))
        `when`(playerRepository.findByTeamId(teamId)).thenReturn(emptyList())

        val result = service.getMostDangerousPlayer(teamId)

        assertNull(result)
    }
}
