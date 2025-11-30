package com.spprj.unq_dapps._s2_GrupoG.service.impl

import com.spprj.unq_dapps._s2_GrupoG.controller.dtos.TeamComparisonResultDTO
import com.spprj.unq_dapps._s2_GrupoG.controller.dtos.TeamMetricComparisonDTO
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.stereotype.Service

@Service
class TeamComparisonServiceImpl(
    private val teamService: TeamServiceImpl,
    private val meterRegistry: MeterRegistry
) {

    private val comparisonCounter = meterRegistry.counter("team_comparisons_total")

    fun compareTeams(teamAId: String, teamBId: String): TeamComparisonResultDTO {

        comparisonCounter.increment()

        val teamA = teamService.getTeamById(teamAId)
        val teamB = teamService.getTeamById(teamBId)

        val metricsA = teamService.getTeamMetrics(teamAId)
        val metricsB = teamService.getTeamMetrics(teamBId)

        val comparisons = mutableListOf<TeamMetricComparisonDTO>()

        val allMetrics = metricsA.keys + metricsB.keys

        allMetrics.forEach { metric ->
            val aVal = metricsA[metric] ?: 0.0
            val bVal = metricsB[metric] ?: 0.0

            val better = when {
                aVal > bVal -> "teamA"
                bVal > aVal -> "teamB"
                else -> "equal"
            }

            comparisons.add(
                TeamMetricComparisonDTO(
                    metric = metric,
                    teamA = aVal,
                    teamB = bVal,
                    better = better
                )
            )
        }

        return TeamComparisonResultDTO(
            teamA = teamA.name,
            teamB = teamB.name,
            metrics = comparisons
        )
    }
}
