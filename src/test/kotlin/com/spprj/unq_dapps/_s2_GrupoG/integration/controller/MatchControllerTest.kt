package com.spprj.unq_dapps._s2_GrupoG.integration.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.spprj.unq_dapps._s2_GrupoG.controller.MatchController
import com.spprj.unq_dapps._s2_GrupoG.controller.dtos.MatchPredictionRequestDto
import com.spprj.unq_dapps._s2_GrupoG.model.MatchPredictionResult
import com.spprj.unq_dapps._s2_GrupoG.model.Player
import com.spprj.unq_dapps._s2_GrupoG.model.Team
import com.spprj.unq_dapps._s2_GrupoG.service.impl.MatchServiceImpl
import com.spprj.unq_dapps._s2_GrupoG.service.impl.TeamServiceImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class MatchControllerTest {

    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var matchService: MatchServiceImpl

    @Mock
    private lateinit var teamService: TeamServiceImpl

    @InjectMocks
    private lateinit var matchController: MatchController

    private val objectMapper = ObjectMapper()

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mockMvc = MockMvcBuilders.standaloneSetup(matchController).build()
    }

    @Test
    fun `01 - should return prediction successfully`() {
        val homeTeam = Team(id = "1", name = "River", rating = 7.5)
        val awayTeam = Team(id = "2", name = "Boca", rating = 7.0)

        val homePlayers = listOf(
            Player(teamId = "1", name = "Jugador A", matchesPlayed = 10, goals = 5, assists = 3, rating = 7.8),
            Player(teamId = "1", name = "Jugador B", matchesPlayed = 8, goals = 2, assists = 1, rating = 7.2)
        )

        val awayPlayers = listOf(
            Player(teamId = "2", name = "Jugador C", matchesPlayed = 12, goals = 4, assists = 2, rating = 6.9),
            Player(teamId = "2", name = "Jugador D", matchesPlayed = 9, goals = 3, assists = 1, rating = 7.1)
        )

        val mockResult = MatchPredictionResult(
            homeTeam = "River",
            awayTeam = "Boca",
            homeWinProbability = 0.55,
            drawProbability = 0.25,
            awayWinProbability = 0.20
        )

        val request = MatchPredictionRequestDto(homeTeamId = "1", awayTeamId = "2")

        Mockito.`when`(teamService.getTeamById("1")).thenReturn(homeTeam)
        Mockito.`when`(teamService.getTeamById("2")).thenReturn(awayTeam)
        Mockito.`when`(teamService.playersOfTeam("1")).thenReturn(homePlayers)
        Mockito.`when`(teamService.playersOfTeam("2")).thenReturn(awayPlayers)
        Mockito.`when`(matchService.predictMatch(homeTeam, awayTeam, 7.5, 7.0)).thenReturn(mockResult)

        mockMvc.perform(
            post("/matches/prediction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.homeTeam").value("River"))
            .andExpect(jsonPath("$.awayTeam").value("Boca"))
            .andExpect(jsonPath("$.homeWinProbability").value(0.55))
            .andExpect(jsonPath("$.drawProbability").value(0.25))
            .andExpect(jsonPath("$.awayWinProbability").value(0.20))
    }

    @Test
    fun `02 - should handle missing team gracefully`() {
        val request = MatchPredictionRequestDto(homeTeamId = "1", awayTeamId = "99")

        Mockito.`when`(teamService.getTeamById("1")).thenReturn(
            Team(id = "1", name = "River Plate", rating = 7.8)
        )
        Mockito.`when`(teamService.getTeamById("99")).thenThrow(
            IllegalArgumentException("No existe el equipo con ID 99")
        )

        mockMvc.perform(
            post("/matches/prediction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isNotFound) // ← antes era isInternalServerError
            .andExpect(jsonPath("$.error").value("No existe el equipo con ID 99"))
    }


    @Test
    fun `03 - should handle empty players gracefully`() {
        val homeTeam = Team(id = "1", name = "River", rating = 7.5)
        val awayTeam = Team(id = "2", name = "Boca", rating = 7.0)
        val request = MatchPredictionRequestDto(homeTeamId = "1", awayTeamId = "2")

        Mockito.`when`(teamService.getTeamById("1")).thenReturn(homeTeam)
        Mockito.`when`(teamService.getTeamById("2")).thenReturn(awayTeam)
        Mockito.`when`(teamService.playersOfTeam("1")).thenReturn(emptyList())
        Mockito.`when`(teamService.playersOfTeam("2")).thenReturn(emptyList())

        val mockResult = MatchPredictionResult(
            homeTeam = "River",
            awayTeam = "Boca",
            homeWinProbability = 0.33,
            drawProbability = 0.34,
            awayWinProbability = 0.33
        )

        Mockito.`when`(matchService.predictMatch(homeTeam, awayTeam, 0.0, 0.0)).thenReturn(mockResult)

        mockMvc.perform(
            post("/matches/prediction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.homeTeam").value("River"))
            .andExpect(jsonPath("$.awayTeam").value("Boca"))
    }
}
