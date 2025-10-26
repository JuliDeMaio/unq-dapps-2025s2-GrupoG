package com.spprj.unq_dapps._s2_GrupoG.controller.dtos

data class MatchPredictionRequestDto(
    val homeTeamId: String,
    val awayTeamId: String
) {
    init {
        require(homeTeamId.isNotBlank()) { "El ID del equipo local no puede estar vacío" }
        require(awayTeamId.isNotBlank()) { "El ID del equipo visitante no puede estar vacío" }
        require(homeTeamId != awayTeamId) { "Los equipos deben ser distintos" }
    }
}
