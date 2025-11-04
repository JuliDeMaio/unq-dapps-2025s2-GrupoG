package com.spprj.unq_dapps._s2_GrupoG.unit.service

import com.spprj.unq_dapps._s2_GrupoG.external.dto.UpcomingMatchDTO
import com.spprj.unq_dapps._s2_GrupoG.external.footballdata.FootballDataService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.http.*
import org.springframework.web.client.RestTemplate

class FootballDataServiceTest {

    private val apiUrl = "https://fake.api"
    private val apiKey = "fake-key"

    @Test
    fun `01 - should return list of upcoming matches`() {
        val restTemplate = mockk<RestTemplate>()
        val service = FootballDataService(apiUrl, apiKey, restTemplate)

        val fakeResponseBody = mapOf(
            "matches" to listOf(
                mapOf(
                    "homeTeam" to mapOf("name" to "Arsenal"),
                    "awayTeam" to mapOf("name" to "Chelsea"),
                    "utcDate" to "2025-12-10T15:00:00Z"
                ),
                mapOf(
                    "homeTeam" to mapOf("name" to "Liverpool"),
                    "awayTeam" to mapOf("name" to "Man City"),
                    "utcDate" to "2025-12-15T18:30:00Z"
                )
            )
        )

        every {
            restTemplate.exchange(
                any<String>(),
                HttpMethod.GET,
                any<HttpEntity<String>>(),
                Map::class.java
            )
        } returns ResponseEntity(fakeResponseBody, HttpStatus.OK)

        val result = service.getUpcomingMatches("61")

        assertEquals(2, result.size)
        assertEquals("Arsenal", result[0].homeTeam)
        assertEquals("Chelsea", result[0].awayTeam)
        assertEquals("Liverpool", result[1].homeTeam)
        assertEquals("Man City", result[1].awayTeam)
    }

    @Test
    fun `02 - should return empty list when matches key missing`() {
        val restTemplate = mockk<RestTemplate>()
        val service = FootballDataService(apiUrl, apiKey, restTemplate)

        every {
            restTemplate.exchange(
                any<String>(),
                HttpMethod.GET,
                any<HttpEntity<String>>(),
                Map::class.java
            )
        } returns ResponseEntity(mapOf("wrongKey" to "oops"), HttpStatus.OK)

        val result = service.getUpcomingMatches("61")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `03 - should return empty list when response body null`() {
        val restTemplate = mockk<RestTemplate>()
        val service = FootballDataService(apiUrl, apiKey, restTemplate)

        every {
            restTemplate.exchange(
                any<String>(),
                HttpMethod.GET,
                any<HttpEntity<String>>(),
                Map::class.java
            )
        } returns ResponseEntity(null, HttpStatus.OK)

        val result = service.getUpcomingMatches("61")
        assertTrue(result.isEmpty())
    }
}
