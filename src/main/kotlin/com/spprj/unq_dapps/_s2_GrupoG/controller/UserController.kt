package com.spprj.unq_dapps._s2_GrupoG.controller

import com.spprj.unq_dapps._s2_GrupoG.model.User
import com.spprj.unq_dapps._s2_GrupoG.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

@RestController
@RequestMapping("/users")
@Tag(name = "Usuarios", description = "Operaciones de gestión de usuarios")
@SecurityRequirement(name = "bearerAuth")
@ApiResponse(responseCode = "401", description = "No autenticado: falta token o es inválido")
class UserController(
    private val userService: UserService
) {

    @GetMapping
    @Operation(summary = "Obtener todos los usuarios")
    @ApiResponse(responseCode = "401", description = "No autenticado: falta token o es inválido")
    fun getAllUsers(): List<User> = userService.findAll()

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un usuario por ID")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @ApiResponse(responseCode = "401", description = "No autenticado: falta token o es inválido")
    fun getUserById(@PathVariable id: Long): ResponseEntity<User> {
        val user = userService.findById(id)
        return if (user != null) ResponseEntity.ok(user)
        else ResponseEntity.notFound().build()
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un usuario existente")
    @ApiResponse(responseCode = "200", description = "Usuario actualizado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @ApiResponse(responseCode = "401", description = "No autenticado: falta token o es inválido")
    fun updateUser(@PathVariable id: Long, @RequestBody user: User): ResponseEntity<User> {
        val updated = userService.update(id, user)
        return if (updated != null) ResponseEntity.ok(updated)
        else ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un usuario")
    @ApiResponse(responseCode = "204", description = "Usuario eliminado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @ApiResponse(responseCode = "401", description = "No autenticado: falta token o es inválido")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Unit> {
        return if (userService.delete(id)) ResponseEntity.noContent().build()
        else ResponseEntity.notFound().build()
    }

    @GetMapping("/{id}/queries")
    fun getUserQueries(
        @PathVariable id: Long,
        @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate
    ): ResponseEntity<Map<String, Any>> {
        val queries = userService.getUserQueriesByDate(id, date)

        val response = mapOf(
            "userId" to id,
            "date" to date,
            "queries" to queries
        )

        return ResponseEntity.ok(response)
    }

}