package com.spprj.unq_dapps._s2_GrupoG.unit.model

import com.spprj.unq_dapps._s2_GrupoG.model.MatchPredictionResult
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("unitTest")
class MatchPredictionResultTest {

    @Test
    fun `01 - should create MatchPredictionResult with correct values`() {
        val result = MatchPredictionResult(
            homeTeam = "River",
            awayTeam = "Boca",
            homeWinProbability = 0.5,
            drawProbability = 0.3,
            awayWinProbability = 0.2
        )

        assertEquals("River", result.homeTeam)
        assertEquals("Boca", result.awayTeam)
        assertEquals(0.5, result.homeWinProbability)
        assertEquals(0.3, result.drawProbability)
        assertEquals(0.2, result.awayWinProbability)
    }
}
