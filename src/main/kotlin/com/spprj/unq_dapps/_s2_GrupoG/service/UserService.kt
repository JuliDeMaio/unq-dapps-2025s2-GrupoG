package com.spprj.unq_dapps._s2_GrupoG.service

import com.spprj.unq_dapps._s2_GrupoG.model.User
import com.spprj.unq_dapps._s2_GrupoG.model.UserQueryLog
import java.time.LocalDate

interface UserService {
    fun findAll(): List<User>
    fun findById(id: Long): User?
    fun save(user: User): User
    fun update(id: Long, user: User): User?
    fun delete(id: Long): Boolean

    fun saveQueryLog(
        userId: Long,
        endpoint: String,
        method: String,
        requestBody: Any?,
        responseBody: Any?
    )

    fun getUserQueriesByDate(userId: Long, date: LocalDate): List<UserQueryLog>

}