package com.spprj.unq_dapps._s2_GrupoG.service.impl

import com.spprj.unq_dapps._s2_GrupoG.repositories.TeamRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class ScraperSchedulerServiceImpl(
    private val playerService: PlayerServiceImpl,
    private val teamRepository: TeamRepository
) {

    @Scheduled(cron = "0 0 */4 * * *") // cada 4 horas
    fun scheduledPopulate() {
        println("▶️ Ejecutando scheduler de scraping...")

        val teams = teamRepository.findAll()
        println("📌 Se encontraron ${teams.size} equipos en la base de datos")

        teams.forEach { team ->
            println("⚽ Scrapeando equipo ${team.name} (${team.id})")
            try {
                playerService.populateDataBaseFromScrapperService(team.id)
            } catch (e: Exception) {
                println("❌ Error al scrapear equipo ${team.name}: ${e.message}")
            }
        }

        println("✅ Scheduler finalizado")
    }
}