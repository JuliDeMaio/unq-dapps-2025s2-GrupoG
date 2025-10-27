package com.spprj.unq_dapps._s2_GrupoG.model

data class MatchPredictionResult(
    val homeTeam: String,
    val awayTeam: String,
    val homeWinProbability: Double,
    val drawProbability: Double,
    val awayWinProbability: Double
)
