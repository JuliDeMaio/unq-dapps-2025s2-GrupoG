package com.spprj.unq_dapps._s2_GrupoG.unit.model

import com.spprj.unq_dapps._s2_GrupoG.model.UserQueryLog
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@ActiveProfiles("unitTest")
class UserQueryLogTest {

    @Test
    fun `01 - should create UserQueryLog with correct values`() {
        val date = LocalDate.now()
        val log = UserQueryLog(
            id = 1L,
            userId = 10L,
            endpoint = "/teams",
            method = "GET",
            requestBody = "{\"teamId\":\"26\"}",
            responseBody = "{\"status\":\"ok\"}",
            queryDate = date
        )

        assertEquals(1L, log.id)
        assertEquals(10L, log.userId)
        assertEquals("/teams", log.endpoint)
        assertEquals("GET", log.method)
        assertEquals("{\"teamId\":\"26\"}", log.requestBody)
        assertEquals("{\"status\":\"ok\"}", log.responseBody)
        assertEquals(date, log.queryDate)
    }
}
