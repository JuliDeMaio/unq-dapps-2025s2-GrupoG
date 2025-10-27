package com.spprj.unq_dapps._s2_GrupoG.controller.dtos

import com.spprj.unq_dapps._s2_GrupoG.model.MatchPredictionResult

data class MatchPredictionResultDto(
    val homeTeam: String,
    val awayTeam: String,
    val homeWinProbability: Double,
    val drawProbability: Double,
    val awayWinProbability: Double
) {
    companion object {
        fun fromModel(result: MatchPredictionResult): MatchPredictionResultDto {
            return MatchPredictionResultDto(
                homeTeam = result.homeTeam,
                awayTeam = result.awayTeam,
                homeWinProbability = result.homeWinProbability,
                drawProbability = result.drawProbability,
                awayWinProbability = result.awayWinProbability
            )
        }
    }
}
