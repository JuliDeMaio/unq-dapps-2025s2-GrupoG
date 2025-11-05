package com.spprj.unq_dapps._s2_GrupoG.unit.repository

import com.spprj.unq_dapps._s2_GrupoG.model.User
import com.spprj.unq_dapps._s2_GrupoG.model.enum.Role
import com.spprj.unq_dapps._s2_GrupoG.repositories.UserRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("unitTest")
class UserRepositoryTest {

    private val userRepository = mockk<UserRepository>()

    @Test
    fun `01 - should find user by email`() {
        val user = User(name="RepoUser", email="repo@test.com", password="123", role=Role.ADMIN)
        every { userRepository.findByEmail("repo@test.com") } returns user

        val found = userRepository.findByEmail("repo@test.com")
        assertNotNull(found)
        assertEquals("RepoUser", found?.name)
    }

    @Test
    fun `02 - should return null when user not found`() {
        every { userRepository.findByEmail("notfound@test.com") } returns null
        val found = userRepository.findByEmail("notfound@test.com")
        assertNull(found)
    }

}