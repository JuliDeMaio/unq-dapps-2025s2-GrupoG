package com.spprj.unq_dapps._s2_GrupoG.integration.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.spprj.unq_dapps._s2_GrupoG.controller.PlayerController
import com.spprj.unq_dapps._s2_GrupoG.controller.dtos.PlayerDangerScoreDTO
import com.spprj.unq_dapps._s2_GrupoG.external.dto.PlayerHistoryDTO
import com.spprj.unq_dapps._s2_GrupoG.service.impl.DangerScoreServiceImpl
import com.spprj.unq_dapps._s2_GrupoG.service.impl.PlayerServiceImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class PlayerControllerTest {

    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var playerService: PlayerServiceImpl

    @Mock
    private lateinit var dangerScoreService: DangerScoreServiceImpl

    private lateinit var token: String

    @InjectMocks
    private lateinit var playerController: PlayerController

    private val objectMapper = ObjectMapper()

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mockMvc = MockMvcBuilders.standaloneSetup(playerController).build()
        token = "test-token"
    }

    @Test
    fun `01 - should return player history when exists`() {
        val mockHistory = PlayerHistoryDTO(
            appearances = 30,
            goals = 15,
            assists = 5,
            yellowCards = 2,
            redCards = 0,
            minutesPlayed = 2400,
            averageRating = 7.8
        )

        Mockito.`when`(playerService.getPlayerHistory("123", "messi")).thenReturn(mockHistory)

        mockMvc.perform(
            get("/players/123/history/messi")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.appearances").value(30))
            .andExpect(jsonPath("$.goals").value(15))
            .andExpect(jsonPath("$.assists").value(5))
            .andExpect(jsonPath("$.averageRating").value(7.8))
    }

    @Test
    fun `02 - should return ok with null body when player history not found`() {
        Mockito.`when`(playerService.getPlayerHistory("999", "unknown")).thenReturn(null)

        mockMvc.perform(
            get("/players/999/history/unknown")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `03 - should return 4xx when player name missing`() {
        mockMvc.perform(
            get("/players/123/history/")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError)
    }

    @Test
    fun `04 - should return a player's danger score`() {
        val teamId = "26"
        val playerId = "123"

        val fakeDto = PlayerDangerScoreDTO(
            playerName = "Lionel Messi",
            dangerScore = 7.80,
            yellowCards = 1,
            redCards = 0,
            minutesPlayed = 850
        )

        `when`(dangerScoreService.calculateDangerScore(teamId, playerId))
            .thenReturn(fakeDto)

        mockMvc.perform(
            get("/players/$teamId/danger/$playerId")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.playerName").value("Lionel Messi"))
            .andExpect(jsonPath("$.dangerScore").value(7.80))
            .andExpect(jsonPath("$.yellowCards").value(1))
            .andExpect(jsonPath("$.redCards").value(0))
            .andExpect(jsonPath("$.minutesPlayed").value(850))
    }

    @Test
    fun `05 - should return 404 when dangerScore player not found`() {
        val teamId = "26"
        val playerId = "999"

        `when`(dangerScoreService.calculateDangerScore(teamId, playerId))
            .thenReturn(null)

        mockMvc.perform(
            get("/players/$teamId/danger/$playerId")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
    }

}
