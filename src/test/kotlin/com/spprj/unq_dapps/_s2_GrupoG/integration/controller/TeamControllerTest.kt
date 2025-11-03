package com.spprj.unq_dapps._s2_GrupoG.integration.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.spprj.unq_dapps._s2_GrupoG.external.footballdata.FootballDataService
import com.spprj.unq_dapps._s2_GrupoG.model.Player
import com.spprj.unq_dapps._s2_GrupoG.model.User
import com.spprj.unq_dapps._s2_GrupoG.model.enum.Role
import com.spprj.unq_dapps._s2_GrupoG.repositories.UserRepository
import com.spprj.unq_dapps._s2_GrupoG.security.JwtTokenProvider
import com.spprj.unq_dapps._s2_GrupoG.service.impl.PlayerServiceImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(properties = [
    "footballdata.api.base-url=https://fake.api",
    "footballdata.api.token=fake-token"
])
@AutoConfigureMockMvc
@ActiveProfiles("integrationTest")
@Transactional
class TeamControllerTest {

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var userRepository: UserRepository
    @Autowired lateinit var passwordEncoder: PasswordEncoder
    @Autowired lateinit var jwtTokenProvider: JwtTokenProvider
    @Autowired lateinit var objectMapper: ObjectMapper

    @MockBean lateinit var playerService: PlayerServiceImpl
    @MockBean lateinit var footballDataService: FootballDataService

    private lateinit var token: String

    @BeforeEach
    fun setup() {
        val user = userRepository.save(
            User(
                name = "TeamUser",
                email = "team@test.com",
                password = passwordEncoder.encode("1234"),
                role = Role.ADMIN
            )
        )
        token = jwtTokenProvider.generateToken(user)
    }

    @Test
    fun `01 - should return players list`() {
        val fakeTeamId = "26"
        val fakePlayers = listOf(
            Player(
                id = 1,
                teamId = fakeTeamId,
                name = "Lionel Messi",
                matchesPlayed = 10,
                goals = 8,
                assists = 5,
                rating = 9.1
            ),
            Player(
                id = 2,
                teamId = fakeTeamId,
                name = "Ángel Di María",
                matchesPlayed = 9,
                goals = 3,
                assists = 4,
                rating = 8.4
            )
        )

        `when`(playerService.getPlayersFromDb(anyString())).thenReturn(fakePlayers)

        mockMvc.perform(
            get("/teams/$fakeTeamId/players")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].name").value("Lionel Messi"))
            .andExpect(jsonPath("$[1].name").value("Ángel Di María"))
    }
}
