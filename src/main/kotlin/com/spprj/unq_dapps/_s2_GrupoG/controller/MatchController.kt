package com.spprj.unq_dapps._s2_GrupoG.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import com.spprj.unq_dapps._s2_GrupoG.controller.dtos.MatchPredictionRequestDto
import com.spprj.unq_dapps._s2_GrupoG.controller.dtos.MatchPredictionResultDto
import com.spprj.unq_dapps._s2_GrupoG.service.impl.MatchServiceImpl
import com.spprj.unq_dapps._s2_GrupoG.service.impl.TeamServiceImpl

@Tag(name = "Partidos", description = "Endpoints relacionados a los partidos")
@RestController
@CrossOrigin
@RequestMapping("/matches")
class MatchController(
    private val matchService: MatchServiceImpl,
    private val teamService: TeamServiceImpl
) {
    @Operation(summary = "Predicción de partido", description = "Devuelve probabilidades de victoria, empate o derrota entre dos equipos")
    @PostMapping("/prediction")
    fun predictMatch(@RequestBody request: MatchPredictionRequestDto): MatchPredictionResultDto {
        val homeTeam = teamService.getTeamById(request.homeTeamId)
        val awayTeam = teamService.getTeamById(request.awayTeamId)

        val homePlayers = teamService.playersOfTeam(request.homeTeamId)
        val awayPlayers = teamService.playersOfTeam(request.awayTeamId)

        val homeRating = homePlayers.mapNotNull { it.rating }.average().takeIf { !it.isNaN() } ?: 0.0
        val awayRating = awayPlayers.mapNotNull { it.rating }.average().takeIf { !it.isNaN() } ?: 0.0

        val result = matchService.predictMatch(homeTeam, awayTeam, homeRating, awayRating)
        return MatchPredictionResultDto.fromModel(result)
    }
}