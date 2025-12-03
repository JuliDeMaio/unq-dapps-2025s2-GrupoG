package com.spprj.unq_dapps._s2_GrupoG.controller

import com.spprj.unq_dapps._s2_GrupoG.config.TeamIdMapping
import com.spprj.unq_dapps._s2_GrupoG.external.dto.UpcomingMatchDTO
import com.spprj.unq_dapps._s2_GrupoG.external.footballdata.FootballDataService
import com.spprj.unq_dapps._s2_GrupoG.model.Player
import com.spprj.unq_dapps._s2_GrupoG.service.impl.PlayerServiceImpl
import com.spprj.unq_dapps._s2_GrupoG.service.impl.TeamComparisonServiceImpl
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import com.spprj.unq_dapps._s2_GrupoG.controller.dtos.TeamMostDangerousPlayerDTO
import com.spprj.unq_dapps._s2_GrupoG.service.impl.DangerScoreServiceImpl
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/teams")
@Tag(name = "Equipos", description = "Endpoints de equipos")
@SecurityRequirement(name = "bearerAuth")
class TeamController(
    private val playerService: PlayerServiceImpl,
    private val footballDataService: FootballDataService,
    private val comparisonService: TeamComparisonServiceImpl,
    private val dangerScoreService: DangerScoreServiceImpl
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
    @Operation(summary = "Comparación de métricas entre dos equipos")
    fun compareTeams(
        @RequestParam teamA: String,
        @RequestParam teamB: String
    ): ResponseEntity<Any> {

        val teamAName = TeamIdMapping.whoScoredToFootballData.keys.find { it == teamA }
            ?: return ResponseEntity.badRequest().body("Team A not found")

        val teamBName = TeamIdMapping.whoScoredToFootballData.keys.find { it == teamB }
            ?: return ResponseEntity.badRequest().body("Team B not found")

        val comparison = comparisonService.compareTeams(teamA, teamB)   // ✔️ USAMOS EL ATRIBUTO

        return ResponseEntity.ok(
            mapOf(
                "teamA" to teamA,
                "teamB" to teamB,
                "metrics" to comparison
            )
        )
    }


    @GetMapping("/force-scrape/{teamId}")
    @Operation(summary = "Fuerza el scrapping y actualización de jugadores del equipo")
    fun forceScrape(@PathVariable teamId: String): ResponseEntity<String> {
        return try {
            playerService.populateDataBaseFromScrapperService(teamId)
            ResponseEntity.ok("Scraping ejecutado correctamente para el equipo $teamId")
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body("Error al ejecutar el scraping: ${e.message}")
        }
    }

    @GetMapping("/{teamId}/most-dangerous-player")
    fun getMostDangerousPlayer(@PathVariable teamId: String): ResponseEntity<Any> {
        val result = dangerScoreService.getMostDangerousPlayer(teamId)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(result)
    }

}