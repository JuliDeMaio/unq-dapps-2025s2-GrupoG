package com.spprj.unq_dapps._s2_GrupoG.integration.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.spprj.unq_dapps._s2_GrupoG.controller.MatchController
import com.spprj.unq_dapps._s2_GrupoG.controller.dtos.MatchPredictionRequestDto
import com.spprj.unq_dapps._s2_GrupoG.model.MatchPredictionResult
import com.spprj.unq_dapps._s2_GrupoG.service.impl.MatchServiceImpl
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
        val request = MatchPredictionRequestDto(homeTeamId = "1", awayTeamId = "2")

        val mockResult = MatchPredictionResult(
            homeTeam = "River",
            awayTeam = "Boca",
            homeWinProbability = 0.55,
            drawProbability = 0.25,
            awayWinProbability = 0.20
        )

        Mockito.`when`(matchService.predict(request)).thenReturn(mockResult)

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
    fun `02 - should handle empty players gracefully`() {
        val request = MatchPredictionRequestDto(homeTeamId = "1", awayTeamId = "2")

        val mockResult = MatchPredictionResult(
            homeTeam = "River",
            awayTeam = "Boca",
            homeWinProbability = 0.33,
            drawProbability = 0.34,
            awayWinProbability = 0.33
        )

        Mockito.`when`(matchService.predict(request)).thenReturn(mockResult)

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
