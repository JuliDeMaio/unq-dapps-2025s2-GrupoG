package com.spprj.unq_dapps._s2_GrupoG.controller

import com.spprj.unq_dapps._s2_GrupoG.external.footballdata.FootballDataService
import com.spprj.unq_dapps._s2_GrupoG.model.Player
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
    private val footballDataService: FootballDataService
) {
    @GetMapping("/{teamId}/players")
    @Operation(summary = "Jugadores de un equipo")
    fun getPlayers(@PathVariable teamId: Long): ResponseEntity<List<Player>> {
        return ResponseEntity.ok(footballDataService.playersOfTeam(teamId))
    }
}
