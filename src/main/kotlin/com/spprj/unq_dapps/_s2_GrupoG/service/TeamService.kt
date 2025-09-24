package com.spprj.unq_dapps._s2_GrupoG.service

import com.spprj.unq_dapps._s2_GrupoG.model.Player

interface TeamService {
    fun playersOfTeam(teamId: String): List<Player>
}