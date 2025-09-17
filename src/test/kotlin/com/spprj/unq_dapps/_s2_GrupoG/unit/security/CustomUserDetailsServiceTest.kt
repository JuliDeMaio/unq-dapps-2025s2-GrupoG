package com.spprj.unq_dapps._s2_GrupoG.unit.security

import com.spprj.unq_dapps._s2_GrupoG.model.User
import com.spprj.unq_dapps._s2_GrupoG.model.enum.Role
import com.spprj.unq_dapps._s2_GrupoG.repositories.UserRepository
import com.spprj.unq_dapps._s2_GrupoG.security.CustomUserDetailsService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("unitTest")
class CustomUserDetailsServiceTest {

    private val userRepository = mockk<UserRepository>()
    private val service = CustomUserDetailsService(userRepository)

    @Test
    fun `01 - should load user by username`() {
        val user = User(name="ServiceUser", email="service@test.com", password="123", role=Role.ADMIN)
        every { userRepository.findByEmail("service@test.com") } returns user

        val details = service.loadUserByUsername("service@test.com")
        assertEquals("service@test.com", details.username)
    }

    @Test
    fun `02 - should throw exception if user not found`() {
        every { userRepository.findByEmail("missing@test.com") } returns null
        assertThrows(UsernameNotFoundException::class.java) {
            service.loadUserByUsername("missing@test.com")
        }
    }
}