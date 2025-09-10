package com.spprj.unq_dapps._s2_GrupoG.controller

import com.spprj.unq_dapps._s2_GrupoG.model.User
import com.spprj.unq_dapps._s2_GrupoG.repositories.UserRepository
import com.spprj.unq_dapps._s2_GrupoG.security.JwtService
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager
) {

    @PostMapping("/register")
    fun register(@RequestBody request: User): ResponseEntity<User> {
        // Encriptar la password
        request.password = passwordEncoder.encode(request.password)
        val savedUser = userRepository.save(request)
        return ResponseEntity.ok(savedUser)
    }

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<Map<String, String>> {
        val authToken = UsernamePasswordAuthenticationToken(loginRequest.email, loginRequest.password)
        authenticationManager.authenticate(authToken)
        val user = userRepository.findByEmail(loginRequest.email)
            ?: return ResponseEntity.badRequest().build()
        val token = jwtService.generateToken(user)
        return ResponseEntity.ok(mapOf("token" to token))
    }
}

// DTO para login
data class LoginRequest(
    val email: String,
    val password: String
)
