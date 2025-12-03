package com.spprj.unq_dapps._s2_GrupoG.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.spprj.unq_dapps._s2_GrupoG.model.User
import com.spprj.unq_dapps._s2_GrupoG.model.UserQueryLog
import com.spprj.unq_dapps._s2_GrupoG.repositories.UserQueryLogRepository
import com.spprj.unq_dapps._s2_GrupoG.repositories.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val userQueryLogRepository: UserQueryLogRepository,
    private val passwordEncoder: PasswordEncoder,
    private val objectMapper: ObjectMapper
) {

    fun findAll(): List<User> = userRepository.findAll()

    fun findById(id: Long): User? =
        userRepository.findById(id).orElse(null)

    @Transactional
    fun save(user: User): User {
        user.password = passwordEncoder.encode(user.password)
        return userRepository.save(user)
    }

    @Transactional
    fun update(id: Long, user: User): User? {
        val existing = userRepository.findById(id).orElse(null) ?: return null
        existing.name = user.name
        existing.email = user.email
        existing.role = user.role
        return userRepository.save(existing)
    }

    @Transactional
    fun delete(id: Long): Boolean {
        return if (userRepository.existsById(id)) {
            userRepository.deleteById(id)
            true
        } else false
    }

    @Transactional
    fun saveQueryLog(
        userId: Long,
        endpoint: String,
        method: String,
        requestBody: Any?,
        responseBody: Any?,
        pathParams: Map<String, Any?>,
        queryParams: Map<String, Any?>
    ) {
        val log = UserQueryLog(
            userId = userId,
            endpoint = endpoint,
            method = method,
            requestBody = requestBody?.let { objectMapper.writeValueAsString(it) },
            responseBody = responseBody?.let { objectMapper.writeValueAsString(it) },
            queryDate = LocalDate.now(),
            timestamp = LocalDateTime.now(),
            pathParams = objectMapper.writeValueAsString(pathParams),
            queryParams = objectMapper.writeValueAsString(queryParams)
        )
        userQueryLogRepository.save(log)
    }

    fun getUserQueriesByDate(userId: Long, date: LocalDate): List<Map<String, Any?>> {
        val logs = userQueryLogRepository.findByUserIdAndQueryDate(userId, date)

        return logs.map { log ->
            mapOf(
                "endpoint" to log.endpoint,
                "method" to log.method,
                "timestamp" to log.timestamp,
                "queryParams" to parseJsonSafely(log.queryParams),
                "pathParams" to parseJsonSafely(log.pathParams),
                "requestBody" to parseJsonSafely(log.requestBody),
                "responseBody" to parseJsonSafely(log.responseBody),
            )
        }
    }

    private fun parseJsonSafely(json: String?): Any? {
        if (json.isNullOrBlank()) return null
        return try {
            objectMapper.readValue(json, Any::class.java)
        } catch (_: Exception) {
            json
        }
    }
}
