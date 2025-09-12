package com.spprj.unq_dapps._s2_GrupoG.controller

import com.spprj.unq_dapps._s2_GrupoG.model.User
import com.spprj.unq_dapps._s2_GrupoG.repositories.UserRepository
import com.spprj.unq_dapps._s2_GrupoG.security.JwtTokenProvider
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "Registro y login de usuarios")
class AuthController(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val authenticationManager: AuthenticationManager
) {

    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo usuario")
    @ApiResponse(responseCode = "200", description = "Usuario registrado")
    fun register(@RequestBody request: User): ResponseEntity<User> {
        request.password = passwordEncoder.encode(request.password)
        val savedUser = userRepository.save(request)
        return ResponseEntity.ok(savedUser)
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión y obtener token JWT")
    @ApiResponse(responseCode = "200", description = "Login exitoso, devuelve token JWT")
    @ApiResponse(responseCode = "400", description = "Credenciales inválidas")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<Map<String, String>> {
        val authToken = UsernamePasswordAuthenticationToken(loginRequest.email, loginRequest.password)
        authenticationManager.authenticate(authToken)

        val user = userRepository.findByEmail(loginRequest.email)
            ?: return ResponseEntity.badRequest().build()

        val token = jwtTokenProvider.generateToken(user)
        return ResponseEntity.ok(mapOf("token" to token))
    }
}

data class LoginRequest(
    val email: String,
    val password: String
)