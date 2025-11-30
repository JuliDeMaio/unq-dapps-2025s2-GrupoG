package com.spprj.unq_dapps._s2_GrupoG.controller

import com.spprj.unq_dapps._s2_GrupoG.config.TeamIdMapping
import com.spprj.unq_dapps._s2_GrupoG.controller.dtos.TeamComparisonResultDTO
import com.spprj.unq_dapps._s2_GrupoG.external.dto.UpcomingMatchDTO
import com.spprj.unq_dapps._s2_GrupoG.external.footballdata.FootballDataService
import com.spprj.unq_dapps._s2_GrupoG.model.Player
import com.spprj.unq_dapps._s2_GrupoG.service.impl.PlayerServiceImpl
import com.spprj.unq_dapps._s2_GrupoG.service.impl.TeamComparisonServiceImpl
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/teams")
@Tag(name = "Equipos", description = "Endpoints de equipos")
@SecurityRequirement(name = "bearerAuth")
class TeamController(
    private val playerService: PlayerServiceImpl,
    private val footballDataService: FootballDataService,
    private val comparisonService: TeamComparisonServiceImpl
) {

    @GetMapping("/{teamId}/players")
    @Operation(summary = "Jugadores de un equipo con estadísticas")
    fun getPlayers(@PathVariable teamId: String): ResponseEntity<List<Player>> {
        val players = playerService.getPlayersFromDb(teamId)
        return ResponseEntity.ok(players)
    }

    @GetMapping("/{teamId}/upcoming-matches")
    fun getUpcomingMatches(@PathVariable teamId: String): ResponseEntity<List<UpcomingMatchDTO>> {
        val fdId = TeamIdMapping.whoScoredToFootballData[teamId]
            ?: return ResponseEntity.badRequest().build()

        val matches = footballDataService.getUpcomingMatches(fdId)
        return ResponseEntity.ok(matches)
    }

    @GetMapping("/compare")
    @Operation(
        summary = "Comparación de equipos",
        description = "Devuelve métricas comparativas entre dos equipos usando estadísticas agregadas"
    )
    fun compareTeams(
        @RequestParam teamA: String,
        @RequestParam teamB: String
    ): ResponseEntity<TeamComparisonResultDTO> {

        val result = comparisonService.compareTeams(teamA, teamB)
        return ResponseEntity.ok(result)
    }
}