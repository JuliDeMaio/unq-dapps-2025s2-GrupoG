package com.spprj.unq_dapps._s2_GrupoG.integration.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.spprj.unq_dapps._s2_GrupoG.external.footballdata.FootballDataService
import com.spprj.unq_dapps._s2_GrupoG.model.User
import com.spprj.unq_dapps._s2_GrupoG.model.enum.Role
import com.spprj.unq_dapps._s2_GrupoG.repositories.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(properties = [
    "footballdata.api.base-url=https://fake.api",
    "footballdata.api.token=fake-token"
])
@AutoConfigureMockMvc
@ActiveProfiles("integrationTest")
@TestInstance(PER_CLASS)
@Transactional
class AuthControllerTest {

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var objectMapper: ObjectMapper
    @Autowired lateinit var passwordEncoder: PasswordEncoder
    @Autowired lateinit var userRepository: UserRepository

    @MockBean lateinit var authenticationManager: AuthenticationManager
    @MockBean lateinit var footballDataService: FootballDataService

    private lateinit var testUser: User

    @BeforeEach
    fun setUp() {
        userRepository.deleteAll()
        testUser = userRepository.save(
            User(
                name = "Test User",
                email = "test${System.currentTimeMillis()}@example.com",
                password = passwordEncoder.encode("pass"),
                role = Role.ADMIN
            )
        )

        // Mockeamos autenticación exitosa
        Mockito.`when`(
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(testUser.email, "pass")
            )
        ).thenReturn(Mockito.mock(org.springframework.security.core.Authentication::class.java))
    }

    @Test
    fun `01 - should register new user`() {
        val newUser = User(
            name = "Another User",
            email = "another${System.currentTimeMillis()}@example.com",
            password = "123456",
            role = Role.ADMIN
        )

        mockMvc.perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.email").value(newUser.email))
    }

    @Test
    fun `02 - should login and return token`() {
        val loginPayload = mapOf("email" to testUser.email, "password" to "pass")

        mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginPayload))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").exists())
    }

    @Test
    fun `03 - should fail login with wrong password`() {
        val loginPayload = mapOf("email" to testUser.email, "password" to "wrongpass")

        // Mockeamos fallo de autenticación
        Mockito.`when`(
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(testUser.email, "wrongpass")
            )
        ).thenThrow(BadCredentialsException("Bad credentials"))

        mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginPayload))
        )
            .andExpect(status().is4xxClientError)
    }
}
