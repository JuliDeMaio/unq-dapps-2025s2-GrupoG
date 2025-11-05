package com.spprj.unq_dapps._s2_GrupoG.unit.repository

import com.spprj.unq_dapps._s2_GrupoG.model.Player
import com.spprj.unq_dapps._s2_GrupoG.repositories.PlayerRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("unitTest")
class PlayerRepositoryTest {

    private val playerRepository = mockk<PlayerRepository>()

    @Test
    fun `01 - should return players by team id`() {
        val teamId = "26"
        val players = listOf(
            Player(id = 1L, teamId = teamId, name = "Messi", matchesPlayed = 10, goals = 8, assists = 5, rating = 9.2),
            Player(id = 2L, teamId = teamId, name = "Di María", matchesPlayed = 9, goals = 3, assists = 4, rating = 8.4)
        )

        every { playerRepository.findByTeamId(teamId) } returns players

        val result = playerRepository.findByTeamId(teamId)
        assertEquals(2, result.size)
        assertEquals("Messi", result.first().name)
    }
}
