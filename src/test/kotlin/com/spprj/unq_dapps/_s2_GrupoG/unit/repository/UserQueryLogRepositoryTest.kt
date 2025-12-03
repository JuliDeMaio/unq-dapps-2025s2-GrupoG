package com.spprj.unq_dapps._s2_GrupoG.unit.repository

import com.spprj.unq_dapps._s2_GrupoG.model.UserQueryLog
import com.spprj.unq_dapps._s2_GrupoG.repositories.UserQueryLogRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.time.LocalDateTime

@ActiveProfiles("unitTest")
class UserQueryLogRepositoryTest {

    private val logRepository = mockk<UserQueryLogRepository>()

    @Test
    fun `01 - should return logs by user id and date`() {
        val today = LocalDate.now()
        val timestamp = LocalDateTime.now()

        val logs = listOf(
            UserQueryLog(
                id = 1L,
                userId = 10L,
                endpoint = "/teams",
                method = "GET",
                requestBody = "{}",
                responseBody = "{}",
                queryDate = today,
                timestamp = timestamp,
                pathParams = null,
                queryParams = null
            )
        )

        every { logRepository.findByUserIdAndQueryDate(10L, today) } returns logs

        val result = logRepository.findByUserIdAndQueryDate(10L, today)

        assertEquals(1, result.size)
        assertEquals("/teams", result.first().endpoint)
        assertEquals("GET", result.first().method)
        assertEquals(10L, result.first().userId)
        assertEquals(today, result.first().queryDate)
    }
}
