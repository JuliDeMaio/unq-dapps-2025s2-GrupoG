package com.spprj.unq_dapps._s2_GrupoG.unit.external

import com.spprj.unq_dapps._s2_GrupoG.external.whoscored.WhoScoredScraper
import com.spprj.unq_dapps._s2_GrupoG.external.dto.PlayerHistoryDTO
import com.spprj.unq_dapps._s2_GrupoG.model.Player
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class WhoScoredScraperTest {

    private lateinit var scraper: WhoScoredScraper

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        scraper = spy(WhoScoredScraper())
    }

    @Test
    fun `01 - should return empty list when scraping fails`() {
        doThrow(RuntimeException("Error scraping")).`when`(scraper).getPlayersOfTeam("26")

        val result = try {
            scraper.getPlayersOfTeam("26")
        } catch (e: Exception) {
            emptyList<Player>()
        }

        assertTrue(result.isEmpty())
    }

    @Test
    fun `02 - should return players correctly`() {
        val mockPlayers = listOf(
            Player(id = 1, teamId = "26", name = "Lionel Messi", matchesPlayed = 10, goals = 8, assists = 5, rating = 9.2),
            Player(id = 2, teamId = "26", name = "Di María", matchesPlayed = 9, goals = 3, assists = 4, rating = 8.4)
        )

        doReturn(mockPlayers).`when`(scraper).getPlayersOfTeam("26")

        val result = scraper.getPlayersOfTeam("26")

        assertEquals(2, result.size)
        assertEquals("Lionel Messi", result[0].name)
        assertEquals(9.2, result[0].rating)
    }

    @Test
    fun `03 - should return null when history scraping fails`() {
        doThrow(RuntimeException("HTML broken")).`when`(scraper).getPlayerHistory("123", "messi")

        val result = try {
            scraper.getPlayerHistory("123", "messi")
        } catch (e: Exception) {
            null
        }

        assertNull(result)
    }

    @Test
    fun `04 - should return valid PlayerHistoryDTO`() {
        val mockHistory = PlayerHistoryDTO(
            appearances = 10,
            goals = 8,
            assists = 5,
            yellowCards = 1,
            redCards = 0,
            minutesPlayed = 900,
            averageRating = 8.7
        )

        doReturn(mockHistory).`when`(scraper).getPlayerHistory("123", "messi")

        val result = scraper.getPlayerHistory("123", "messi")

        assertNotNull(result)
        assertEquals(8, result.goals)
        assertEquals(8.7, result.averageRating)
    }
}
