package com.spprj.unq_dapps._s2_GrupoG.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.spprj.unq_dapps._s2_GrupoG.model.User
import com.spprj.unq_dapps._s2_GrupoG.model.UserQueryLog
import com.spprj.unq_dapps._s2_GrupoG.repositories.UserQueryLogRepository
import com.spprj.unq_dapps._s2_GrupoG.repositories.UserRepository
import com.spprj.unq_dapps._s2_GrupoG.service.UserService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val userQueryLogRepository: UserQueryLogRepository,
    private val passwordEncoder: PasswordEncoder,
    private val objectMapper: ObjectMapper
) : UserService {

    override fun findAll(): List<User> = userRepository.findAll()

    override fun findById(id: Long): User? =
        userRepository.findById(id).orElse(null)

    override fun save(user: User): User {
        user.password = passwordEncoder.encode(user.password)
        return userRepository.save(user)
    }

    override fun update(id: Long, user: User): User? {
        val existing = userRepository.findById(id).orElse(null) ?: return null
        existing.name = user.name
        existing.email = user.email
        existing.role = user.role
        return userRepository.save(existing)
    }

    override fun delete(id: Long): Boolean {
        return if (userRepository.existsById(id)) {
            userRepository.deleteById(id)
            true
        } else false
    }

    override fun saveQueryLog(
        userId: Long,
        endpoint: String,
        method: String,
        requestBody: Any?,
        responseBody: Any?
    ) {
        val log = UserQueryLog(
            userId = userId,
            endpoint = endpoint,
            method = method,
            requestBody = requestBody?.let { objectMapper.writeValueAsString(it) },
            responseBody = responseBody?.let { objectMapper.writeValueAsString(it) },
            queryDate = LocalDate.now()
        )
        userQueryLogRepository.save(log)
    }

    override fun getUserQueriesByDate(userId: Long, date: LocalDate): List<UserQueryLog> {
        return userQueryLogRepository.findByUserIdAndQueryDate(userId, date)
    }

}