package com.spprj.unq_dapps._s2_GrupoG.config

import com.spprj.unq_dapps._s2_GrupoG.model.Team
import com.spprj.unq_dapps._s2_GrupoG.repositories.TeamRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TeamsDataInitializer {

    private val logger = LoggerFactory.getLogger(TeamsDataInitializer::class.java)

    @Bean
    fun initTeams(teamRepository: TeamRepository) = CommandLineRunner {
        if (teamRepository.count() == 0L) {
            val teams = listOf(
                Team("26", "Liverpool"),
                Team("167", "Manchester City"),
                Team("15", "Chelsea"),
                Team("13", "Arsenal"),
                Team("30", "Tottenham Hotspur"),
                Team("32", "Manchester United"),
                Team("31", "Everton"),
                Team("23", "Newcastle United"),
                Team("24", "Aston Villa"),
                Team("14", "Leicester City"),
                Team("18", "Southampton"),
                Team("29", "West Ham United"),
                Team("162", "Crystal Palace"),
                Team("211", "Brighton & Hove Albion"),
                Team("170", "Fulham"),
                Team("189", "Brentford"),
                Team("174", "Nottingham Forest"),
                Team("184", "Burnley"),
                Team("163", "Sheffield United"),
                Team("161", "Wolves")
            )

            teamRepository.saveAll(teams)
            logger.info("{} teams were inserted into the database", teams.size)
        } else {
            logger.info("Database already has data, no duplicates were inserted")
        }
    }
}
