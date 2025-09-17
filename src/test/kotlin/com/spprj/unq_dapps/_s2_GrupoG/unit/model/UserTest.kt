package com.spprj.unq_dapps._s2_GrupoG.unit.model

import com.spprj.unq_dapps._s2_GrupoG.model.User
import com.spprj.unq_dapps._s2_GrupoG.model.enum.Role
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("unitTest")
class UserTest {

    @Test
    fun `01 - should create user with values`() {
        val user = User(name="Juli", email="juli@test.com", password="123", role=Role.ADMIN)
        assertEquals("Juli", user.name)
        assertEquals("juli@test.com", user.email)
        assertEquals("123", user.password)
        assertEquals(Role.ADMIN, user.role)
    }
}