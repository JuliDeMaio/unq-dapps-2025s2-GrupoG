package com.spprj.unq_dapps._s2_GrupoG.unit.service

import com.spprj.unq_dapps._s2_GrupoG.controller.dtos.MatchPredictionRequestDto
import com.spprj.unq_dapps._s2_GrupoG.external.footballdata.FootballDataService
import com.spprj.unq_dapps._s2_GrupoG.model.Player
import com.spprj.unq_dapps._s2_GrupoG.model.Team
import com.spprj.unq_dapps._s2_GrupoG.service.impl.MatchServiceImpl
import com.spprj.unq_dapps._s2_GrupoG.service.impl.TeamServiceImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MatchServiceImplTest {

    private lateinit var teamService: TeamServiceImpl
    private lateinit var matchService: MatchServiceImpl
    private lateinit var footballDataService: FootballDataService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        teamService = Mockito.mock(TeamServiceImpl::class.java)
        footballDataService = Mockito.mock(FootballDataService::class.java)
        matchService = MatchServiceImpl(teamService, footballDataService)
    }

    private fun buildPlayer(teamId: String, name: String, rating: Double): Player {
        return Player(
            id = null,
            teamId = teamId,
            name = name,
            matchesPlayed = 10,
            goals = 0,
            assists = 0,
            rating = rating,
            yellowCards = 0,
            redCards = 0,
            minutesPlayed = 1000,
            whoScoredId = ""
        )
    }

    @Test
    fun `01 - should favor home team when higher rating`() {
        val home = Team("1", "Boca", 8.0)
        val away = Team("2", "River", 6.0)

        Mockito.`when`(teamService.getTeamById("1")).thenReturn(home)
        Mockito.`when`(teamService.getTeamById("2")).thenReturn(away)

        Mockito.`when`(teamService.playersOfTeam("1")).thenReturn(listOf(buildPlayer("1","A",8.0)))
        Mockito.`when`(teamService.playersOfTeam("2")).thenReturn(listOf(buildPlayer("2","B",6.0)))

        val result = matchService.predict(MatchPredictionRequestDto("1","2"))

        assertTrue(result.homeWinProbability > result.awayWinProbability)
    }

    @Test
    fun `02 - should be balanced when ratings are equal`() {
        val home = Team("1", "Racing", 7.0)
        val away = Team("2", "San Lorenzo", 7.0)

        Mockito.`when`(teamService.getTeamById("1")).thenReturn(home)
        Mockito.`when`(teamService.getTeamById("2")).thenReturn(away)

        Mockito.`when`(teamService.playersOfTeam("1")).thenReturn(listOf(buildPlayer("1","A",7.0)))
        Mockito.`when`(teamService.playersOfTeam("2")).thenReturn(listOf(buildPlayer("2","B",7.0)))

        val result = matchService.predict(MatchPredictionRequestDto("1","2"))

        assertTrue(result.homeWinProbability in 0.3..0.7)
        assertTrue(result.awayWinProbability in 0.3..0.7)
        assertTrue(result.drawProbability > 0.2)
    }

    @Test
    fun `03 - should handle invalid ratings gracefully`() {
        val home = Team("1", "Defensa", Double.NaN)
        val away = Team("2", "Tigre", Double.POSITIVE_INFINITY)

        Mockito.`when`(teamService.getTeamById("1")).thenReturn(home)
        Mockito.`when`(teamService.getTeamById("2")).thenReturn(away)

        Mockito.`when`(teamService.playersOfTeam("1")).thenReturn(emptyList())
        Mockito.`when`(teamService.playersOfTeam("2")).thenReturn(emptyList())

        val result = matchService.predict(MatchPredictionRequestDto("1","2"))

        assertTrue(result.homeWinProbability in 0.0..1.0)
        assertTrue(result.awayWinProbability in 0.0..1.0)
        assertTrue(result.drawProbability in 0.0..1.0)
    }

    @Test
    fun `04 - probabilities must sum to 1`() {
        val home = Team("1", "Estudiantes", 6.5)
        val away = Team("2", "Lanús", 5.5)

        Mockito.`when`(teamService.getTeamById("1")).thenReturn(home)
        Mockito.`when`(teamService.getTeamById("2")).thenReturn(away)

        Mockito.`when`(teamService.playersOfTeam("1")).thenReturn(listOf(buildPlayer("1","A",6.5)))
        Mockito.`when`(teamService.playersOfTeam("2")).thenReturn(listOf(buildPlayer("2","B",5.5)))

        val result = matchService.predict(MatchPredictionRequestDto("1","2"))

        val total = result.homeWinProbability + result.awayWinProbability + result.drawProbability
        assertEquals(1.0, total, 0.01)
    }
}
