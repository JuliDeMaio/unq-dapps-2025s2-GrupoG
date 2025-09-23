package com.spprj.unq_dapps._s2_GrupoG.external.footballdata.dtos

data class FootballDataMatchesResponse(
    val matches: List<MatchSummary> = emptyList()
)

data class MatchSummary(
    val id: Long,
    val utcDate: String? = null,
    val status: String? = null
)