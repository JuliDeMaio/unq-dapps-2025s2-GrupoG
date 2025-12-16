package com.spprj.unq_dapps._s2_GrupoG.service.impl

import com.spprj.unq_dapps._s2_GrupoG.repositories.TeamRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class ScraperSchedulerServiceImpl(
    private val playerService: PlayerServiceImpl,
    private val teamRepository: TeamRepository
) {

    private val logger = LoggerFactory.getLogger(ScraperSchedulerServiceImpl::class.java)

    @Scheduled(cron = "0 0 */4 * * *")
    fun scheduledPopulate() {
        logger.info("Executing scraping scheduler...")

        val teams = teamRepository.findAll().toList()
        logger.info("Founded {} teams at database", teams.size)

        teams.forEach { team ->
            logger.info("Scraping team {} ({})", team.name, team.id)
            try {
                playerService.populateDataBaseFromScrapperService(team.id)
            } catch (e: Exception) {
                logger.error("Error at scrapping team {}", team.name, e)
            }
        }

        logger.info("Scheduler finished")
    }
}
