package com.spprj.unq_dapps._s2_GrupoG.service.impl

import com.spprj.unq_dapps._s2_GrupoG.model.User
import com.spprj.unq_dapps._s2_GrupoG.repositories.UserRepository
import com.spprj.unq_dapps._s2_GrupoG.service.UserService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
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
}