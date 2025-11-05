package com.spprj.unq_dapps._s2_GrupoG.unit.repository

import com.spprj.unq_dapps._s2_GrupoG.model.Team
import com.spprj.unq_dapps._s2_GrupoG.repositories.TeamRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import java.util.*

@ActiveProfiles("unitTest")
class TeamRepositoryTest {

    private val teamRepository = mockk<TeamRepository>()

    @Test
    fun `01 - should find team by id`() {
        val team = Team(id = "26", name = "Liverpool", rating = 8.4)
        every { teamRepository.findById("26") } returns Optional.of(team)

        val result = teamRepository.findById("26").orElse(null)
        assertNotNull(result)
        assertEquals("Liverpool", result?.name)
        assertEquals(8.4, result?.rating)
    }
}
