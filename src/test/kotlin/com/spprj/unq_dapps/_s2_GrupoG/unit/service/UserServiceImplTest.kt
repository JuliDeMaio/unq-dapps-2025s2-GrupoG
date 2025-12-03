package com.spprj.unq_dapps._s2_GrupoG.unit.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.spprj.unq_dapps._s2_GrupoG.model.User
import com.spprj.unq_dapps._s2_GrupoG.model.UserQueryLog
import com.spprj.unq_dapps._s2_GrupoG.model.enum.Role
import com.spprj.unq_dapps._s2_GrupoG.repositories.UserQueryLogRepository
import com.spprj.unq_dapps._s2_GrupoG.repositories.UserRepository
import com.spprj.unq_dapps._s2_GrupoG.service.impl.UserServiceImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UserServiceImplTest {

    @Mock lateinit var userRepository: UserRepository
    @Mock lateinit var userQueryLogRepository: UserQueryLogRepository
    @Mock lateinit var passwordEncoder: PasswordEncoder
    @Mock lateinit var objectMapper: ObjectMapper

    @InjectMocks lateinit var userService: UserServiceImpl

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `01 - should return all users`() {
        val users = listOf(User(1, "A", "a@test.com", "123", Role.ADMIN))
        `when`(userRepository.findAll()).thenReturn(users)

        val result = userService.findAll()

        assertEquals(1, result.size)
        verify(userRepository).findAll()
    }

    @Test
    fun `02 - should find user by id`() {
        val user = User(1, "A", "a@test.com", "123", Role.ADMIN)
        `when`(userRepository.findById(1L)).thenReturn(Optional.of(user))

        val result = userService.findById(1L)

        assertNotNull(result)
        assertEquals("A", result?.name)
    }

    @Test
    fun `03 - should return null when user not found`() {
        `when`(userRepository.findById(99L)).thenReturn(Optional.empty())

        val result = userService.findById(99L)

        assertNull(result)
    }

    @Test
    fun `04 - should save user with encoded password`() {
        val user = User(1, "A", "a@test.com", "raw", Role.ADMIN)
        `when`(passwordEncoder.encode("raw")).thenReturn("encoded")
        `when`(userRepository.save(any(User::class.java))).thenReturn(user)

        val result = userService.save(user)

        verify(passwordEncoder).encode("raw")
        verify(userRepository).save(any(User::class.java))
        assertEquals("encoded", result.password)
    }

    @Test
    fun `05 - should update existing user`() {
        val existing = User(1, "Old", "old@test.com", "123", Role.ADMIN)
        val updated = User(1, "New", "new@test.com", "456", Role.ADMIN)
        `when`(userRepository.findById(1L)).thenReturn(Optional.of(existing))
        `when`(userRepository.save(any(User::class.java))).thenReturn(updated)

        val result = userService.update(1L, updated)

        assertNotNull(result)
        assertEquals("New", result?.name)
        verify(userRepository).save(existing)
    }

    @Test
    fun `06 - should return null when updating non-existing user`() {
        `when`(userRepository.findById(1L)).thenReturn(Optional.empty())

        val result = userService.update(1L, User(1, "X", "x@test.com", "p", Role.ADMIN))

        assertNull(result)
        verify(userRepository, never()).save(any(User::class.java))
    }

    @Test
    fun `07 - should delete user when exists`() {
        `when`(userRepository.existsById(1L)).thenReturn(true)

        val result = userService.delete(1L)

        assertTrue(result)
        verify(userRepository).deleteById(1L)
    }

    @Test
    fun `08 - should not delete when user does not exist`() {
        `when`(userRepository.existsById(1L)).thenReturn(false)

        val result = userService.delete(1L)

        assertTrue(!result)
        verify(userRepository, never()).deleteById(anyLong())
    }

    @Test
    fun `09 - should save query log with correct JSON`() {
        val date = LocalDate.now()
        val request = mapOf("req" to "value")
        val response = mapOf("res" to "value")

        `when`(objectMapper.writeValueAsString(request)).thenReturn("{\"req\":\"value\"}")
        `when`(objectMapper.writeValueAsString(response)).thenReturn("{\"res\":\"value\"}")

        userService.saveQueryLog(
            1L,
            "/endpoint",
            "GET",
            request,
            response,
            emptyMap(),
            emptyMap()
        )

        val captor = ArgumentCaptor.forClass(UserQueryLog::class.java)
        verify(userQueryLogRepository).save(captor.capture())

        val saved = captor.value
        assertEquals(1L, saved.userId)
        assertEquals("/endpoint", saved.endpoint)
        assertEquals("GET", saved.method)
        assertEquals("{\"req\":\"value\"}", saved.requestBody)
        assertEquals("{\"res\":\"value\"}", saved.responseBody)
        assertEquals(date, saved.queryDate)
    }

    @Test
    fun `10 - should get user queries by date`() {
        val today = LocalDate.now()
        val logs = listOf(
            UserQueryLog(
                id = 1L,
                userId = 1L,
                endpoint = "/x",
                method = "GET",
                requestBody = "{}",
                responseBody = "{}",
                queryDate = today,
                timestamp = LocalDateTime.now(),
                pathParams = null,
                queryParams = null
            )
        )

        `when`(userQueryLogRepository.findByUserIdAndQueryDate(1L, today))
            .thenReturn(logs)

        val result = userService.getUserQueriesByDate(1L, today)

        assertEquals(1, result.size)
        verify(userQueryLogRepository).findByUserIdAndQueryDate(1L, today)
    }
}
