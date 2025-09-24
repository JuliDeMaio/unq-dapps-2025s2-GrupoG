package com.spprj.unq_dapps._s2_GrupoG.integration.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.spprj.unq_dapps._s2_GrupoG.model.User
import com.spprj.unq_dapps._s2_GrupoG.model.enum.Role
import com.spprj.unq_dapps._s2_GrupoG.repositories.UserRepository
import com.spprj.unq_dapps._s2_GrupoG.security.JwtTokenProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integrationTest")
@Transactional
class UserControllerTest {

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var objectMapper: ObjectMapper
    @Autowired lateinit var userRepository: UserRepository
    @Autowired lateinit var passwordEncoder: PasswordEncoder
    @Autowired lateinit var jwtTokenProvider: JwtTokenProvider

    private lateinit var token: String

    @BeforeEach
    fun setup() {
        val user = userRepository.save(User(name="Admin", email="admin@test.com",
            password=passwordEncoder.encode("1234"), role=Role.ADMIN))
        token = jwtTokenProvider.generateToken(user)
    }

    @Test
    fun `01 - should get all users`() {
        mockMvc.perform(get("/users")
            .header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)
    }

    @Test
    fun `02 - should get user by id`() {
        val u = userRepository.save(User(name="Juli", email="juli@test.com", password="123", role=Role.ADMIN))

        mockMvc.perform(get("/users/${u.id}")
            .header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value("juli@test.com"))
    }

    @Test
    fun `03 - should return 404 when user not found`() {
        mockMvc.perform(get("/users/999")
            .header("Authorization", "Bearer $token"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `04 - should update user`() {
        val u = userRepository.save(User(name="ToUpdate", email="upd@test.com", password="123", role=Role.ADMIN))
        val updatePayload = User(
            id = u.id,
            name = "UpdatedName",
            email = u.email,
            password = u.password,
            role = u.role
        )

        mockMvc.perform(put("/users/${u.id}")
            .header("Authorization", "Bearer $token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatePayload)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("UpdatedName"))
    }

    @Test
    fun `05 - should delete user`() {
        val u = userRepository.save(User(name="DeleteMe", email="del@test.com", password="123", role=Role.ADMIN))

        mockMvc.perform(delete("/users/${u.id}")
            .header("Authorization", "Bearer $token"))
            .andExpect(status().isNoContent)
    }
}