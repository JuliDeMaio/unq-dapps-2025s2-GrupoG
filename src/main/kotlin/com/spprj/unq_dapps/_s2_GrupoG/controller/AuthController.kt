package com.spprj.unq_dapps._s2_GrupoG.controller

import com.spprj.unq_dapps._s2_GrupoG.model.User
import com.spprj.unq_dapps._s2_GrupoG.service.impl.AuthServiceImpl
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "Registro y login de usuarios")
class AuthController(
    private val authService: AuthServiceImpl
) {

    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo usuario")
    @ApiResponse(responseCode = "200", description = "Usuario registrado")
    fun register(@RequestBody request: User): ResponseEntity<User> {
        val saved = authService.register(request)
        return ResponseEntity.ok(saved)
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión y obtener token JWT")
    @ApiResponse(responseCode = "200", description = "Login exitoso, devuelve token JWT")
    @ApiResponse(responseCode = "400", description = "Credenciales inválidas")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<Map<String, String>> {
        val token = authService.login(loginRequest.email, loginRequest.password)
        return ResponseEntity.ok(mapOf("token" to token))
    }
}

data class LoginRequest(
    val email: String,
    val password: String
)
