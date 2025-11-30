package com.spprj.unq_dapps._s2_GrupoG.controller.dtos

data class TeamMetricComparisonDTO(
    val metric: String,
    val teamA: Double,
    val teamB: Double,
    val better: String
)

data class TeamComparisonResultDTO(
    val teamA: String,
    val teamB: String,
    val metrics: List<TeamMetricComparisonDTO>
)