package com.spprj.unq_dapps._s2_GrupoG.integration.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.spprj.unq_dapps._s2_GrupoG.external.whoscored.WhoScoredScraper
import com.spprj.unq_dapps._s2_GrupoG.model.Player
import com.spprj.unq_dapps._s2_GrupoG.model.User
import com.spprj.unq_dapps._s2_GrupoG.model.enum.Role
import com.spprj.unq_dapps._s2_GrupoG.repositories.UserRepository
import com.spprj.unq_dapps._s2_GrupoG.security.JwtTokenProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import org.mockito.Mockito.`when`

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integrationTest")
@Transactional
class TeamControllerTest {

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var userRepository: UserRepository
    @Autowired lateinit var passwordEncoder: PasswordEncoder
    @Autowired lateinit var jwtTokenProvider: JwtTokenProvider
    @Autowired lateinit var objectMapper: ObjectMapper

    @MockBean lateinit var whoScoredScraper: WhoScoredScraper

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
        val fakePlayers = listOf(
            Player("Messi", 10, 5, 3, 8.9),
            Player("Cristiano", 12, 7, 2, 8.5)
        )

        `when`(whoScoredScraper.getPlayersOfTeam("26")).thenReturn(fakePlayers)

        mockMvc.perform(get("/teams/26/players")
            .header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].name").value("Messi"))
            .andExpect(jsonPath("$[1].name").value("Cristiano"))
    }
}