package com.spprj.unq_dapps._s2_GrupoG.service.impl

import com.spprj.unq_dapps._s2_GrupoG.external.whoscored.WhoScoredScraper
import com.spprj.unq_dapps._s2_GrupoG.model.Player
import com.spprj.unq_dapps._s2_GrupoG.service.TeamService
import org.springframework.stereotype.Service

@Service
class TeamServiceImpl(
    private val whoScoredScraper: WhoScoredScraper
) : TeamService {
    override fun playersOfTeam(teamId: String): List<Player> {
        return whoScoredScraper.getPlayersOfTeam(teamId)
    }
}