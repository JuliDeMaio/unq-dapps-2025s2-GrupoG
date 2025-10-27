package com.spprj.unq_dapps._s2_GrupoG.service.impl

import com.spprj.unq_dapps._s2_GrupoG.model.MatchPredictionResult
import com.spprj.unq_dapps._s2_GrupoG.model.Team
import org.springframework.stereotype.Service
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.sqrt

@Service
class MatchServiceImpl {

    fun predictMatch(homeTeam: Team, awayTeam: Team, homeRating: Double, awayRating: Double): MatchPredictionResult {
        val mu = 7.0
        val sigma = 0.28
        val k = 0.7
        val homeAdvZ = 0.25
        val nu = 0.315

        val hR = if (homeRating.isFinite()) homeRating else mu
        val aR = if (awayRating.isFinite()) awayRating else mu

        var zHome = (hR - mu) / sigma
        var zAway = (aR - mu) / sigma
        zHome += homeAdvZ

        val sHome = exp(k * zHome)
        val sAway = exp(k * zAway)

        val cross = 2.0 * nu * sqrt(sHome * sAway)
        val denom = sHome + sAway + cross

        val pHome = sHome / denom
        val pAway = sAway / denom
        val pDraw = cross / denom

        return MatchPredictionResult(
            homeTeam = homeTeam.name,
            awayTeam = awayTeam.name,
            homeWinProbability = pHome.coerceIn(0.0, 1.0),
            drawProbability = pDraw.coerceIn(0.0, 1.0),
            awayWinProbability = pAway.coerceIn(0.0, 1.0)
        )
    }
}

