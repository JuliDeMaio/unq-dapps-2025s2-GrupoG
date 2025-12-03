package com.spprj.unq_dapps._s2_GrupoG.unit.service

import com.spprj.unq_dapps._s2_GrupoG.model.Team
import com.spprj.unq_dapps._s2_GrupoG.service.impl.TeamComparisonServiceImpl
import com.spprj.unq_dapps._s2_GrupoG.service.impl.TeamServiceImpl
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

class TeamComparisonServiceImplTest {

    private lateinit var teamService: TeamServiceImpl
    private lateinit var meterRegistry: MeterRegistry
    private lateinit var counter: Counter
    private lateinit var service: TeamComparisonServiceImpl

    @BeforeEach
    fun setup() {
        teamService = mock(TeamServiceImpl::class.java)
        meterRegistry = mock(MeterRegistry::class.java)
        counter = mock(Counter::class.java)

        `when`(meterRegistry.counter("team_comparisons_total")).thenReturn(counter)

        service = TeamComparisonServiceImpl(teamService, meterRegistry)
    }

    @Test
    fun `01 - should compare teams and return metrics correctly`() {
        val teamAId = "15"
        val teamBId = "13"

        `when`(teamService.getTeamById(teamAId)).thenReturn(Team(teamAId, "Chelsea", 0.0))
        `when`(teamService.getTeamById(teamBId)).thenReturn(Team(teamBId, "Arsenal", 0.0))

        `when`(teamService.getTeamMetrics(teamAId)).thenReturn(
            mapOf("goalsPerMatch" to 0.50, "averageRating" to 7.25)
        )

        `when`(teamService.getTeamMetrics(teamBId)).thenReturn(
            mapOf("goalsPerMatch" to 0.40, "averageRating" to 7.30)
        )

        val result = service.compareTeams(teamAId, teamBId)

        assertEquals("Chelsea", result.teamA)
        assertEquals("Arsenal", result.teamB)

        assertEquals(2, result.metrics.size)

        val gpm = result.metrics.first { it.metric == "goalsPerMatch" }
        assertEquals(0.50, gpm.teamA)
        assertEquals(0.40, gpm.teamB)
        assertEquals("Chelsea", gpm.better)

        val ar = result.metrics.first { it.metric == "averageRating" }
        assertEquals("Arsenal", ar.better)

        verify(counter, times(1)).increment()
    }

    @Test
    fun `02 - should handle missing metrics on one team`() {
        val teamAId = "1"
        val teamBId = "2"

        `when`(teamService.getTeamById(teamAId)).thenReturn(Team(teamAId, "TeamA", 0.0))
        `when`(teamService.getTeamById(teamBId)).thenReturn(Team(teamBId, "TeamB", 0.0))

        `when`(teamService.getTeamMetrics(teamAId)).thenReturn(
            mapOf("goals" to 10.0)
        )

        `when`(teamService.getTeamMetrics(teamBId)).thenReturn(
            mapOf("assists" to 5.0)
        )

        val result = service.compareTeams(teamAId, teamBId)

        assertEquals(2, result.metrics.size)

        val goals = result.metrics.first { it.metric == "goals" }
        assertEquals(10.0, goals.teamA)
        assertEquals(0.0, goals.teamB)

        val assists = result.metrics.first { it.metric == "assists" }
        assertEquals(0.0, assists.teamA)
        assertEquals(5.0, assists.teamB)
    }

    @Test
    fun `03 - should handle empty metrics`() {
        val teamAId = "A"
        val teamBId = "B"

        `when`(teamService.getTeamById(teamAId)).thenReturn(Team(teamAId, "A-Team", 0.0))
        `when`(teamService.getTeamById(teamBId)).thenReturn(Team(teamBId, "B-Team", 0.0))

        `when`(teamService.getTeamMetrics(teamAId)).thenReturn(emptyMap())
        `when`(teamService.getTeamMetrics(teamBId)).thenReturn(emptyMap())

        val result = service.compareTeams(teamAId, teamBId)

        assertTrue(result.metrics.isEmpty())
    }

    @Test
    fun `04 - round2 should format correctly`() {
        val result = 3.14159.round2()
        assertEquals(3.14, result)
    }

    private fun Double.round2(): Double = service.run { this@round2.round2() }
}
