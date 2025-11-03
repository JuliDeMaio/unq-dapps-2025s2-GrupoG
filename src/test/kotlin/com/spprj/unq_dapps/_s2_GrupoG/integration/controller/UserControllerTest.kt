package com.spprj.unq_dapps._s2_GrupoG.integration.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.spprj.unq_dapps._s2_GrupoG.external.footballdata.FootballDataService
import com.spprj.unq_dapps._s2_GrupoG.model.User
import com.spprj.unq_dapps._s2_GrupoG.model.enum.Role
import com.spprj.unq_dapps._s2_GrupoG.repositories.UserRepository
import com.spprj.unq_dapps._s2_GrupoG.security.JwtTokenProvider
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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

@SpringBootTest(properties = [
    "footballdata.api.base-url=https://fake.api",
    "footballdata.api.token=fake-token"
])
@AutoConfigureMockMvc
@ActiveProfiles("integrationTest")
@Transactional
class UserControllerTest {

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var objectMapper: ObjectMapper
    @Autowired lateinit var userRepository: UserRepository
    @Autowired lateinit var passwordEncoder: PasswordEncoder
    @Autowired lateinit var jwtTokenProvider: JwtTokenProvider

    @MockBean lateinit var footballDataService: FootballDataService

    private lateinit var token: String

    @BeforeEach
    fun setup() {
        userRepository.deleteAll()

        val admin = userRepository.save(
            User(
                name = "Admin",
                email = "admin${System.currentTimeMillis()}@test.com",
                password = passwordEncoder.encode("1234"),
                role = Role.ADMIN
            )
        )

        token = jwtTokenProvider.generateToken(admin)
    }

    @Test
    fun `01 - should get all users`() {
        mockMvc.perform(
            get("/users")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `02 - should get user by id`() {
        val user = userRepository.save(
            User(
                name = "Juli",
                email = "juli${System.currentTimeMillis()}@test.com",
                password = passwordEncoder.encode("123"),
                role = Role.ADMIN
            )
        )

        mockMvc.perform(
            get("/users/${user.id}")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value(user.email))
    }

    @Test
    fun `03 - should return 404 when user not found`() {
        mockMvc.perform(
            get("/users/99999")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `04 - should update user`() {
        val existing = userRepository.save(
            User(
                name = "ToUpdate",
                email = "upd${System.currentTimeMillis()}@test.com",
                password = passwordEncoder.encode("123"),
                role = Role.ADMIN
            )
        )

        val updatePayload = User(
            id = existing.id,
            name = "UpdatedName",
            email = existing.email,
            password = existing.password,
            role = existing.role
        )

        mockMvc.perform(
            put("/users/${existing.id}")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatePayload))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("UpdatedName"))
    }

    @Test
    fun `05 - should delete user`() {
        val user = userRepository.save(
            User(
                name = "DeleteMe",
                email = "del${System.currentTimeMillis()}@test.com",
                password = passwordEncoder.encode("123"),
                role = Role.ADMIN
            )
        )

        mockMvc.perform(
            delete("/users/${user.id}")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isNoContent)
    }
}
