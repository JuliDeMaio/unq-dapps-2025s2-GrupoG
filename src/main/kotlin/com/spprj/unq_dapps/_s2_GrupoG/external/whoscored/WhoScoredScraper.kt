package com.spprj.unq_dapps._s2_GrupoG.external.whoscored

import com.spprj.unq_dapps._s2_GrupoG.external.dto.PlayerHistoryDTO
import com.spprj.unq_dapps._s2_GrupoG.model.Player
import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class WhoScoredScraper(
    private val customDriver: WebDriver? = null,
    private val isTestMode: Boolean = false
) {

    companion object {
        private const val INNER_TEXT_SCRIPT = "return arguments[0].innerText;"
    }

    private val driver: WebDriver by lazy {
        customDriver ?: run {
            WebDriverManager.chromedriver().setup()
            val options = ChromeOptions().apply {
                addArguments("--disable-blink-features=AutomationControlled")
                addArguments(
                    "user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                            "AppleWebKit/537.36 (KHTML, like Gecko) " +
                            "Chrome/140.0.7339.208 Safari/537.36"
                )
                addArguments("--headless=new")
                addArguments("--no-sandbox")
                addArguments("--disable-dev-shm-usage")
                addArguments("--disable-extensions")
                addArguments("--remote-allow-origins=*")
                addArguments("--disable-gpu")
                addArguments("--window-size=1920,1080")
            }
            ChromeDriver(options)
        }
    }

    private val wait: WebDriverWait? =
        if (isTestMode) null else WebDriverWait(driver, Duration.ofSeconds(30))

    private fun getCellText(js: JavascriptExecutor, cell: WebElement?): String {
        if (cell == null) return ""
        return try {
            (js.executeScript(INNER_TEXT_SCRIPT, cell) as String).trim()
        } catch (e: Exception) {
            ""
        }
    }

    fun getPlayersOfTeam(teamId: String): List<Player> {
        val url = "https://www.whoscored.com/teams/$teamId/show"
        println("Opening team page: $url")
        @Suppress("ReplaceGetOrSet")
        driver.get(url)

        if (!isTestMode) {
            wait!!.until(ExpectedConditions.presenceOfElementLocated(By.id("team-squad-stats")))
        }

        val rows: List<WebElement> = if (!isTestMode) {
            wait!!.until<List<WebElement>?> {
                val elements = driver.findElements(By.cssSelector("#team-squad-stats tbody tr")).toList()
                if (elements.isNotEmpty()) elements else null
            } ?: emptyList() // 👈 evita null devolviendo lista vacía
        } else {
            driver.findElements(By.cssSelector("#team-squad-stats tbody tr")).toList()
        }

        val players = mutableListOf<Player>()
        println("Detects rows: ${rows.size}")

        val js = driver as JavascriptExecutor

        for (row in rows) {
            try {
                val cells = row.findElements(By.tagName("td")).toList()
                if (cells.isEmpty()) continue

                val nameElement = cells[0].findElement(By.tagName("a"))
                val playerName = (js.executeScript(INNER_TEXT_SCRIPT, nameElement) as String)
                    .trim()
                    .replace(Regex("^\\d+\\s*"), "")
                if (playerName.isBlank()) continue

                val matches = getCellText(js, cells.getOrNull(4)).toIntOrNull() ?: 0
                val goals = getCellText(js, cells.getOrNull(6)).toIntOrNull() ?: 0
                val assists = getCellText(js, cells.getOrNull(7)).toIntOrNull() ?: 0
                val rating = getCellText(js, cells.lastOrNull()).replace(",", ".").toDoubleOrNull()

                println("✅ $playerName → $matches PJ, $goals G, $assists A, $rating Rating")

                players.add(
                    Player(
                        id = null,
                        teamId = teamId,
                        name = playerName,
                        matchesPlayed = matches,
                        goals = goals,
                        assists = assists,
                        rating = rating
                    )
                )
            } catch (e: Exception) {
                println("Error processing row: ${e.message}")
            }
        }

        println("Total extracted players: ${players.size}")
        return players
    }

    fun getPlayerHistory(playerId: String, playerSlug: String): PlayerHistoryDTO? {
        val url = "https://www.whoscored.com/players/$playerId/history/$playerSlug"
        println("📖 Opening player history page: $url")
        @Suppress("ReplaceGetOrSet")
        driver.get(url)

        if (!isTestMode) {
            wait!!.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("#player-table-statistics-body")
                )
            )
        }

        val tbody = driver.findElement(By.cssSelector("#player-table-statistics-body"))
        val rows = tbody.findElements(By.tagName("tr"))
        if (rows.isEmpty()) {
            println("⚠️ No rows found for player $playerId")
            return null
        }

        val lastRow = rows.last()
        val js = driver as JavascriptExecutor

        fun cellText(cell: WebElement?) = try {
            (js.executeScript(INNER_TEXT_SCRIPT, cell) as String).trim()
        } catch (_: Exception) {
            ""
        }

        val cells = lastRow.findElements(By.tagName("td"))
        if (cells.size < 9) {
            println("⚠️ Not enough columns for player $playerId")
            return null
        }

        val appearances = cellText(cells.getOrNull(2)).replace(",", "").toIntOrNull() ?: 0
        val minutesPlayed = cellText(cells.getOrNull(3)).replace(",", "").toIntOrNull() ?: 0
        val goals = cellText(cells.getOrNull(4)).replace(",", "").toIntOrNull() ?: 0
        val assists = cellText(cells.getOrNull(5)).replace(",", "").toIntOrNull() ?: 0
        val yellowCards = cellText(cells.getOrNull(6)).replace(",", "").toIntOrNull() ?: 0
        val redCards = cellText(cells.getOrNull(7)).replace(",", "").toIntOrNull() ?: 0
        val averageRating = cellText(cells.getOrNull(12)).replace(",", ".").toDoubleOrNull()

        val dto = PlayerHistoryDTO(
            appearances = appearances,
            goals = goals,
            assists = assists,
            yellowCards = yellowCards,
            redCards = redCards,
            minutesPlayed = minutesPlayed,
            averageRating = averageRating
        )

        println("✅ Historical stats for player $playerId → $dto")
        return dto
    }
}
