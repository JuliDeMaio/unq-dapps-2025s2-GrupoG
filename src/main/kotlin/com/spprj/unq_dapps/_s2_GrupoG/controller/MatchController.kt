package com.spprj.unq_dapps._s2_GrupoG.controller

import com.spprj.unq_dapps._s2_GrupoG.controller.dtos.MatchPredictionRequestDto
import com.spprj.unq_dapps._s2_GrupoG.controller.dtos.MatchPredictionResultDto
import com.spprj.unq_dapps._s2_GrupoG.service.impl.MatchServiceImpl
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Partidos", description = "Endpoints relacionados a los partidos")
@RestController
@RequestMapping("/matches")
@CrossOrigin
class MatchController(
    private val matchService: MatchServiceImpl
) {

    @Operation(
        summary = "Predicción de partido",
        description = "Devuelve probabilidades de victoria, empate o derrota entre dos equipos"
    )
    @PostMapping("/prediction")
    fun predictMatch(@RequestBody request: MatchPredictionRequestDto)
            = ResponseEntity.ok(
        MatchPredictionResultDto.fromModel(
            matchService.predict(request)
        )
    )
}
