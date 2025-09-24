package com.spprj.unq_dapps._s2_GrupoG.controller

import com.spprj.unq_dapps._s2_GrupoG.model.Player
import com.spprj.unq_dapps._s2_GrupoG.service.impl.TeamServiceImpl
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
    private val teamServiceImpl: TeamServiceImpl
) {
    @GetMapping("/{teamId}/players")
    @Operation(summary = "Jugadores de un equipo con estadísticas desde WhoScored")
    fun getPlayers(@PathVariable teamId: String): ResponseEntity<List<Player>> {
        val players = teamServiceImpl.playersOfTeam(teamId)
        return ResponseEntity.ok(players)
    }
}
