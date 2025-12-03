package com.spprj.unq_dapps._s2_GrupoG.controller

import com.spprj.unq_dapps._s2_GrupoG.controller.dtos.PlayerDangerScoreDTO
import com.spprj.unq_dapps._s2_GrupoG.external.dto.PlayerHistoryDTO
import com.spprj.unq_dapps._s2_GrupoG.service.impl.DangerScoreServiceImpl
import com.spprj.unq_dapps._s2_GrupoG.service.impl.PlayerServiceImpl
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/players")
@Tag(name = "Jugadores", description = "Endpoints relacionados con jugadores")
@SecurityRequirement(name = "bearerAuth")
class PlayerController(
    private val playerService: PlayerServiceImpl,
    private val dangerScoreService: DangerScoreServiceImpl
) {

    @GetMapping("/{playerId}/history/{playerName}")
    @Operation(summary = "Historial completo de un jugador (datos históricos desde WhoScored)")
    fun getPlayerHistory(
        @PathVariable playerId: String,
        @PathVariable playerName: String
    ): ResponseEntity<PlayerHistoryDTO?> {
        val history = playerService.getPlayerHistory(playerId, playerName)
        return ResponseEntity.ok(history)
    }

    @GetMapping("/{playerId}/danger/{playerName}")
    @Operation(summary = "Danger Score basado en estadísticas de temporada")
    fun getPlayerDangerScore(
        @PathVariable playerId: String,
        @PathVariable playerName: String
    ): ResponseEntity<PlayerDangerScoreDTO> {

        val result = dangerScoreService.calculateDangerScore(playerId, playerName)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(result)
    }

}
