package com.spprj.unq_dapps._s2_GrupoG.integration.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.spprj.unq_dapps._s2_GrupoG.controller.PlayerController
import com.spprj.unq_dapps._s2_GrupoG.external.dto.PlayerHistoryDTO
import com.spprj.unq_dapps._s2_GrupoG.service.impl.PlayerServiceImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
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

    @InjectMocks
    private lateinit var playerController: PlayerController

    private val objectMapper = ObjectMapper()

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mockMvc = MockMvcBuilders.standaloneSetup(playerController).build()
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
    fun `03 - should return 400 when player name missing`() {
        mockMvc.perform(
            get("/players/123/history/")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().is4xxClientError)
    }
}
