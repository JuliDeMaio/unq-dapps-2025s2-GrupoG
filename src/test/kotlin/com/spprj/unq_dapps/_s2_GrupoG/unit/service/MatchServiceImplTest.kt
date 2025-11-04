package com.spprj.unq_dapps._s2_GrupoG.unit.service

import com.spprj.unq_dapps._s2_GrupoG.model.Team
import com.spprj.unq_dapps._s2_GrupoG.service.impl.MatchServiceImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MatchServiceImplTest {

    private lateinit var matchService: MatchServiceImpl

    @BeforeEach
    fun setup() {
        matchService = MatchServiceImpl()
    }

    @Test
    fun `01 - should favor home team when higher rating`() {
        val homeTeam = Team(id = "1", name = "Boca Juniors", rating = 8.0)
        val awayTeam = Team(id = "2", name = "River Plate", rating = 6.0)

        val result = matchService.predictMatch(homeTeam, awayTeam, homeTeam.rating ?: 0.0, awayTeam.rating ?: 0.0)

        assertTrue(result.homeWinProbability > result.awayWinProbability)
        assertTrue(result.homeWinProbability + result.awayWinProbability + result.drawProbability in 0.99..1.01)
    }

    @Test
    fun `02 - should be balanced when teams have equal ratings`() {
        val homeTeam = Team(id = "1", name = "Racing", rating = 7.0)
        val awayTeam = Team(id = "2", name = "San Lorenzo", rating = 7.0)

        val result = matchService.predictMatch(homeTeam, awayTeam, homeTeam.rating ?: 0.0, awayTeam.rating ?: 0.0)

        assertTrue(result.homeWinProbability > 0.3)
        assertTrue(result.awayWinProbability > 0.3)
        assertTrue(result.drawProbability > 0.2)
    }

    @Test
    fun `03 - should handle invalid ratings gracefully`() {
        val homeTeam = Team(id = "1", name = "Defensa", rating = Double.NaN)
        val awayTeam = Team(id = "2", name = "Tigre", rating = Double.POSITIVE_INFINITY)

        val result = matchService.predictMatch(homeTeam, awayTeam, homeTeam.rating ?: 0.0, awayTeam.rating ?: 0.0)

        assertTrue(result.homeWinProbability in 0.0..1.0)
        assertTrue(result.awayWinProbability in 0.0..1.0)
        assertTrue(result.drawProbability in 0.0..1.0)
    }

    @Test
    fun `04 - should return all probabilities normalized`() {
        val homeTeam = Team(id = "1", name = "Estudiantes", rating = 6.5)
        val awayTeam = Team(id = "2", name = "Lanús", rating = 5.5)

        val result = matchService.predictMatch(homeTeam, awayTeam, homeTeam.rating ?: 0.0, awayTeam.rating ?: 0.0)
        val total = result.homeWinProbability + result.awayWinProbability + result.drawProbability

        assertEquals(1.0, total, 0.01)
    }
}
