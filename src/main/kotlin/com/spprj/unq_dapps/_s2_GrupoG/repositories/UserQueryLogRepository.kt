package com.spprj.unq_dapps._s2_GrupoG.repositories

import com.spprj.unq_dapps._s2_GrupoG.model.UserQueryLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface UserQueryLogRepository : JpaRepository<UserQueryLog, Long> {
    fun findByUserIdAndQueryDate(userId: Long, queryDate: LocalDate): List<UserQueryLog>
}