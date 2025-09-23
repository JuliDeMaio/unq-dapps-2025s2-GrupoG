package com.spprj.unq_dapps._s2_GrupoG.external.footballdata.dtos

data class FootballDataMatchDetailResponse(
    val id: Long? = null,
    val utcDate: String? = null,
    val status: String? = null,
    val events: List<MatchEvent> = emptyList(),
    val lineups: List<TeamLineup> = emptyList()
)

data class TeamLineup(
    val teamId: Long? = null,
    val teamName: String? = null,
    val formation: String? = null,
    val startXI: List<LineupPlayer> = emptyList(),
    val substitutes: List<LineupPlayer> = emptyList()
)

data class LineupPlayer(
    val id: Long? = null,
    val name: String? = null,
    val position: String? = null,
    val shirtNumber: Int? = null
)

data class MatchEvent(
    val minute: Int? = null,
    val type: String? = null,
    val player: MatchPerson? = null,
    val assist: MatchPerson? = null
)

data class MatchPerson(
    val id: Long? = null,
    val name: String? = null
)
