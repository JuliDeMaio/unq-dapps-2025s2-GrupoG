package com.spprj.unq_dapps._s2_GrupoG.unit.security

import com.spprj.unq_dapps._s2_GrupoG.model.User
import com.spprj.unq_dapps._s2_GrupoG.model.enum.Role
import com.spprj.unq_dapps._s2_GrupoG.security.JwtTokenProvider
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("unitTest")
class JwtTokenProviderTest {

    private val jwtTokenProvider = JwtTokenProvider()
    private val user = User(name="JwtUser", email="jwt@test.com", password="123", role=Role.ADMIN)

    @Test
    fun `01 - should generate valid token`() {
        val token = jwtTokenProvider.generateToken(user)
        assertTrue(jwtTokenProvider.isTokenValid(token, user))
    }

    @Test
    fun `02 - should extract username`() {
        val token = jwtTokenProvider.generateToken(user)
        assertEquals("jwt@test.com", jwtTokenProvider.extractUsername(token))
    }

    @Test
    fun `03 - should extract role`() {
        val token = jwtTokenProvider.generateToken(user)
        assertEquals("ADMIN", jwtTokenProvider.extractRole(token))
    }
}