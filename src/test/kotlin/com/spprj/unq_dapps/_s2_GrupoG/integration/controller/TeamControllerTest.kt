package com.spprj.unq_dapps._s2_GrupoG.integration.controller

import com.spprj.unq_dapps._s2_GrupoG.controller.dtos.TeamComparisonResultDTO
import com.spprj.unq_dapps._s2_GrupoG.controller.dtos.TeamMetricComparisonDTO
import com.spprj.unq_dapps._s2_GrupoG.controller.dtos.TeamMostDangerousPlayerDTO
import com.spprj.unq_dapps._s2_GrupoG.external.dto.UpcomingMatchDTO
import com.spprj.unq_dapps._s2_GrupoG.external.footballdata.FootballDataService
import com.spprj.unq_dapps._s2_GrupoG.model.Player
import com.spprj.unq_dapps._s2_GrupoG.model.User
import com.spprj.unq_dapps._s2_GrupoG.model.enum.Role
import com.spprj.unq_dapps._s2_GrupoG.repositories.UserRepository
import com.spprj.unq_dapps._s2_GrupoG.security.JwtTokenProvider
import com.spprj.unq_dapps._s2_GrupoG.service.impl.DangerScoreServiceImpl
import com.spprj.unq_dapps._s2_GrupoG.service.impl.PlayerServiceImpl
import com.spprj.unq_dapps._s2_GrupoG.service.impl.TeamComparisonServiceImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.kotlin.whenever
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

    @MockBean lateinit var playerService: PlayerServiceImpl
    @MockBean lateinit var dangerScoreService: DangerScoreServiceImpl
    @MockBean lateinit var footballDataService: FootballDataService
    @MockBean lateinit var comparisonService: TeamComparisonServiceImpl

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
        val fakeTeamId = "26" // EXISTE en TeamIdMapping
        val fakePlayers = listOf(
            Player(1, fakeTeamId, "Messi", 10, 8, 5, 9.1, 1, 0, 850, "123"),
            Player(2, fakeTeamId, "Di María", 9, 3, 4, 8.4, 0, 0, 700, "456")
        )

        `when`(playerService.getPlayersFromDb(anyString())).thenReturn(fakePlayers)

        mockMvc.perform(
            get("/teams/$fakeTeamId/players")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].name").value("Messi"))
    }

    @Test
    fun `02 - should return empty list`() {
        val teamId = "26"

        `when`(playerService.getPlayersFromDb(teamId)).thenReturn(emptyList())

        mockMvc.perform(
            get("/teams/$teamId/players")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(0))
    }

    @Test
    fun `03 - should return most dangerous player`() {
        val teamId = "15" // EXISTE en map

        val dto = TeamMostDangerousPlayerDTO(
            "Chelsea", "Enzo Fernández", 8.7
        )

        whenever(dangerScoreService.getMostDangerousPlayer(teamId)).thenReturn(dto)

        mockMvc.perform(
            get("/teams/$teamId/most-dangerous-player")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.teamName").value("Chelsea"))
    }

    @Test
    fun `04 - should return 404 when no dangerous player`() {
        val teamId = "15"

        whenever(dangerScoreService.getMostDangerousPlayer(teamId)).thenReturn(null)

        mockMvc.perform(
            get("/teams/$teamId/most-dangerous-player")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `05 - should return upcoming matches`() {
        val teamId = "26"
        val mappedId = "64"

        val matches = listOf(
            UpcomingMatchDTO("Chelsea", "Arsenal", "2025-01-01"),
            UpcomingMatchDTO("Chelsea", "Liverpool", "2025-01-10")
        )

        whenever(footballDataService.getUpcomingMatches(mappedId)).thenReturn(matches)

        mockMvc.perform(
            get("/teams/$teamId/upcoming-matches")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].homeTeam").value("Chelsea"))
    }

    @Test
    fun `06 - should return 400 when teamId not mapped`() {
        val teamId = "999"

        mockMvc.perform(
            get("/teams/$teamId/upcoming-matches")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `07 - should compare teams successfully`() {
        val teamA = "26"
        val teamB = "15"

        val comparison = TeamComparisonResultDTO(
            teamA = "Liverpool",
            teamB = "Chelsea",
            metrics = listOf(
                TeamMetricComparisonDTO("goalsPerMatch", 0.40, 0.55, "Chelsea")
            )
        )

        whenever(comparisonService.compareTeams(teamA, teamB)).thenReturn(comparison)

        mockMvc.perform(
            get("/teams/compare")
                .param("teamA", teamA)
                .param("teamB", teamB)
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.teamA").value(teamA))
            .andExpect(jsonPath("$.metrics.teamA").value("Liverpool"))
    }

    @Test
    fun `08 - should return 400 when teamA invalid`() {
        val teamA = "999"
        val teamB = "15"

        mockMvc.perform(
            get("/teams/compare")
                .param("teamA", teamA)
                .param("teamB", teamB)
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `09 - should return 400 when teamB invalid`() {
        val teamA = "26"
        val teamB = "777"

        mockMvc.perform(
            get("/teams/compare")
                .param("teamA", teamA)
                .param("teamB", teamB)
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `10 - should force scrape successfully`() {
        val teamId = "26"

        mockMvc.perform(
            get("/teams/force-scrape/$teamId")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").value("Scraping ejecutado correctamente para el equipo $teamId"))
    }

    @Test
    fun `11 - should return 500 when scraping fails`() {
        val teamId = "26"

        whenever(playerService.populateDataBaseFromScrapperService(teamId))
            .thenThrow(RuntimeException("fail"))

        mockMvc.perform(
            get("/teams/force-scrape/$teamId")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isInternalServerError)
    }
}
