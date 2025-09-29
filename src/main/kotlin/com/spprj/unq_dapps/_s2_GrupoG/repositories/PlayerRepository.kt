package com.spprj.unq_dapps._s2_GrupoG.repositories

import com.spprj.unq_dapps._s2_GrupoG.model.Player
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PlayerRepository : JpaRepository<Player, Long> {
    fun findByTeamId(teamId: String): List<Player>
}