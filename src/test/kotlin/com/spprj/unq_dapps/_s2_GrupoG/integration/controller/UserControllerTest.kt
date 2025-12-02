package com.spprj.unq_dapps._s2_GrupoG.integration.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.spprj.unq_dapps._s2_GrupoG.model.User
import com.spprj.unq_dapps._s2_GrupoG.model.UserQueryLog
import com.spprj.unq_dapps._s2_GrupoG.model.enum.Role
import com.spprj.unq_dapps._s2_GrupoG.repositories.UserRepository
import com.spprj.unq_dapps._s2_GrupoG.security.JwtTokenProvider
import com.spprj.unq_dapps._s2_GrupoG.service.impl.UserServiceImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integrationTest")
@Transactional
class UserControllerTest {

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var objectMapper: ObjectMapper
    @Autowired lateinit var jwtTokenProvider: JwtTokenProvider
    @Autowired lateinit var passwordEncoder: PasswordEncoder
    @Autowired lateinit var userRepository: UserRepository

    @MockBean lateinit var userService: UserServiceImpl
    @MockBean lateinit var footballDataService: com.spprj.unq_dapps._s2_GrupoG.external.footballdata.FootballDataService

    private lateinit var token: String
    private val baseUrl = "/users"

    @BeforeEach
    fun setup() {
        userRepository.deleteAll()
        val admin = userRepository.save(
            User(
                name = "Admin",
                email = "admin@test.com",
                password = passwordEncoder.encode("1234"),
                role = Role.ADMIN
            )
        )
        token = jwtTokenProvider.generateToken(admin)
    }

    @Test
    fun `01 - should return all users`() {
        val users = listOf(
            User(1L, "Juli", "juli@test.com", "encoded", Role.ADMIN),
            User(2L, "Guido", "guido@test.com", "encoded", Role.ADMIN)
        )

        Mockito.`when`(userService.findAll()).thenReturn(users)

        mockMvc.perform(
            get(baseUrl).header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].name").value("Juli"))
            .andExpect(jsonPath("$[1].email").value("guido@test.com"))
    }

    @Test
    fun `02 - should return user by id`() {
        val user = User(1L, "Guido", "guido@test.com", "encoded", Role.ADMIN)
        Mockito.`when`(userService.findById(1L)).thenReturn(user)

        mockMvc.perform(
            get("$baseUrl/1").header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value("guido@test.com"))
    }

    @Test
    fun `03 - should return 404 when user not found`() {
        Mockito.`when`(userService.findById(99L)).thenReturn(null)

        mockMvc.perform(
            get("$baseUrl/99").header("Authorization", "Bearer $token")
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `04 - should update user successfully`() {
        val updated = User(1L, "Updated", "upd@test.com", "encoded", Role.ADMIN)
        Mockito.`when`(userService.update(Mockito.eq(1L), anyNonNull())).thenReturn(updated)

        mockMvc.perform(
            put("$baseUrl/1")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Updated"))
    }

    @Test
    fun `05 - should return 404 when updating non-existing user`() {
        Mockito.`when`(userService.update(Mockito.anyLong(), anyNonNull())).thenReturn(null)

        mockMvc.perform(
            put("$baseUrl/99")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        User(name = "X", email = "x@test.com", password = "x", role = Role.ADMIN)
                    )
                )
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `06 - should delete user successfully`() {
        Mockito.`when`(userService.delete(1L)).thenReturn(true)

        mockMvc.perform(
            delete("$baseUrl/1").header("Authorization", "Bearer $token")
        ).andExpect(status().isNoContent)
    }

    @Test
    fun `07 - should return 404 when delete fails`() {
        Mockito.`when`(userService.delete(99L)).thenReturn(false)

        mockMvc.perform(
            delete("$baseUrl/99").header("Authorization", "Bearer $token")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `08 - should return user queries by date`() {
        val logs = listOf(
            mapOf(
                "endpoint" to "/teams",
                "method" to "GET",
                "requestBody" to null,
                "responseBody" to mapOf<String, Any>()
            )
        )

        Mockito.`when`(userService.getUserQueriesByDate(1L, LocalDate.now())).thenReturn(logs)

        mockMvc.perform(
            get("$baseUrl/1/queries?date=${LocalDate.now()}")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.userId").value(1))
            .andExpect(jsonPath("$.queries[0].endpoint").value("/teams"))
    }

}

/** 🔧 Helper para evitar NPE con Mockito + Kotlin **/
@Suppress("UNCHECKED_CAST")
private fun <T> anyNonNull(): T = Mockito.any<T>().also { Unit }
