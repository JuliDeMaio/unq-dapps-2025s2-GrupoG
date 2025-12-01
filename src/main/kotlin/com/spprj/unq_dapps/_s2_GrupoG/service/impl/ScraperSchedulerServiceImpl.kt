package com.spprj.unq_dapps._s2_GrupoG.service.impl

import com.spprj.unq_dapps._s2_GrupoG.repositories.TeamRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class ScraperSchedulerServiceImpl(
    private val playerService: PlayerServiceImpl,
    private val teamRepository: TeamRepository
) {

    @Scheduled(cron = "* * * * * *") // cada 4 horas
    fun scheduledPopulate() {
        println("▶️ Executing scraping scheduler...")

        val teams = teamRepository.findAll()
        println("📌 Founded ${teams.size} teams at database")

        teams.forEach { team ->
            println("⚽ Scraping team ${team.name} (${team.id})")
            try {
                playerService.populateDataBaseFromScrapperService(team.id)
            } catch (e: Exception) {
                println("❌ Error at scrapping team ${team.name}: ${e.message}")
            }
        }

        println("✅ Scheduler finished")
    }
}