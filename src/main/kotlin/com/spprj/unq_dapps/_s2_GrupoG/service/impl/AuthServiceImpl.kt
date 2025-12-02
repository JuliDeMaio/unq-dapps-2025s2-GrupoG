package com.spprj.unq_dapps._s2_GrupoG.service.impl

import com.spprj.unq_dapps._s2_GrupoG.model.User
import com.spprj.unq_dapps._s2_GrupoG.repositories.UserRepository
import com.spprj.unq_dapps._s2_GrupoG.security.JwtTokenProvider
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val authenticationManager: AuthenticationManager
) {

    @Transactional
    fun register(user: User): User {
        user.password = passwordEncoder.encode(user.password)
        return userRepository.save(user)
    }

    fun login(email: String, password: String): String {
        val auth = UsernamePasswordAuthenticationToken(email, password)
        authenticationManager.authenticate(auth)

        val user = userRepository.findByEmail(email)
            ?: throw IllegalArgumentException("Usuario no encontrado")

        return jwtTokenProvider.generateToken(user)
    }
}
