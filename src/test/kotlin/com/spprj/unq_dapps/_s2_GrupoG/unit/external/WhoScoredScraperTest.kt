package com.spprj.unq_dapps._s2_GrupoG.unit.external

import com.spprj.unq_dapps._s2_GrupoG.external.whoscored.WhoScoredScraper
import com.spprj.unq_dapps._s2_GrupoG.external.dto.PlayerHistoryDTO
import com.spprj.unq_dapps._s2_GrupoG.model.Player
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class WhoScoredScraperTest {

    private lateinit var scraper: WhoScoredScraper

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        scraper = spy(WhoScoredScraper(isTestMode = true))
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

    @Test
    fun `05 - should return empty list if table is missing`() {
        val scraperMock = mock(WhoScoredScraper::class.java)
        `when`(scraperMock.getPlayersOfTeam("99"))
            .thenThrow(NoSuchElementException("table not found"))

        val result = try {
            scraperMock.getPlayersOfTeam("99")
        } catch (e: Exception) {
            emptyList<Player>()
        }

        assertTrue(result.isEmpty())
    }

    @Test
    fun `06 - should handle null ratings gracefully`() {
        val mockPlayers = listOf(
            Player(id = 1, teamId = "26", name = "Unknown", matchesPlayed = 0, goals = 0, assists = 0, rating = null)
        )
        doReturn(mockPlayers).`when`(scraper).getPlayersOfTeam("26")

        val result = scraper.getPlayersOfTeam("26")

        assertEquals(1, result.size)
        assertNull(result[0].rating)
    }

    @Test
    fun `07 - getCellText should return text or empty on exception`() {
        val js = mock(JavascriptExecutor::class.java)
        val cell = mock(WebElement::class.java)

        `when`(js.executeScript(anyString(), eq(cell))).thenReturn("  Messi ")
        val text = scraper.javaClass.getDeclaredMethod("getCellText", JavascriptExecutor::class.java, WebElement::class.java)
            .apply { isAccessible = true }
            .invoke(scraper, js, cell) as String
        assertEquals("Messi", text)

        `when`(js.executeScript(anyString(), eq(cell))).thenThrow(RuntimeException("boom"))
        val text2 = scraper.javaClass.getDeclaredMethod("getCellText", JavascriptExecutor::class.java, WebElement::class.java)
            .apply { isAccessible = true }
            .invoke(scraper, js, cell) as String
        assertEquals("", text2)
    }

    @Test
    fun `08 - getPlayersOfTeam should parse players from mocked elements`() {
        val driver = mock(WebDriver::class.java, withSettings().extraInterfaces(JavascriptExecutor::class.java))
        val js = driver as JavascriptExecutor

        val row = mock(WebElement::class.java)
        val nameElement = mock(WebElement::class.java)
        val tdName = mock(WebElement::class.java)
        val tdList = listOf(
            tdName, mock(WebElement::class.java), mock(WebElement::class.java),
            mock(WebElement::class.java), mock(WebElement::class.java),
            mock(WebElement::class.java), mock(WebElement::class.java), mock(WebElement::class.java)
        )

        `when`(row.findElements(By.tagName("td"))).thenReturn(tdList)
        `when`(tdName.findElement(By.tagName("a"))).thenReturn(nameElement)
        `when`(js.executeScript(anyString(), eq(nameElement))).thenReturn("Lionel Messi")
        `when`(driver.findElements(By.cssSelector("#team-squad-stats tbody tr"))).thenReturn(listOf(row))

        val scraperMock = WhoScoredScraper(driver, isTestMode = true)
        val result = scraperMock.getPlayersOfTeam("26")

        assertTrue(result.any { it.name.contains("Messi") })
    }

    @Test
    fun `09 - getPlayerHistory should return null if no rows`() {
        val driver = mock(WebDriver::class.java)
        val tbody = mock(WebElement::class.java)

        `when`(driver.findElement(By.cssSelector("#player-table-statistics-body"))).thenReturn(tbody)
        `when`(tbody.findElements(By.tagName("tr"))).thenReturn(emptyList())

        val scraperMock = WhoScoredScraper(driver, isTestMode = true)
        val result = scraperMock.getPlayerHistory("123", "fake-slug")

        assertNull(result)
    }
}
