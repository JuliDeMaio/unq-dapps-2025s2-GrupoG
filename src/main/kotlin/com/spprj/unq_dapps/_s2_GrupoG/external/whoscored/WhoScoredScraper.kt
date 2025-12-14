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
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class WhoScoredScraper(
    private val customDriver: WebDriver? = null,
    private val isTestMode: Boolean = false
) {

    private val logger = LoggerFactory.getLogger(WhoScoredScraper::class.java)

    companion object {
        private const val INNER_TEXT_SCRIPT = "return arguments[0].innerText;"
    }

    private val driver: WebDriver by lazy {
        customDriver ?: run {
            WebDriverManager.chromedriver().setup()
            val options = ChromeOptions().apply {
                addArguments("--disable-blink-features=AutomationControlled")
                addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
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

    /** -------------------------------------------------------------
     *  SAFE TEXT EXTRACTOR
     * -------------------------------------------------------------- */
    private fun getCellText(js: JavascriptExecutor, cell: WebElement?): String {
        if (cell == null) return ""
        return try {
            (js.executeScript(INNER_TEXT_SCRIPT, cell) as? String)?.trim() ?: ""
        } catch (_: Exception) {
            ""
        }
    }

    /** -------------------------------------------------------------
     *  SCRAPE PLAYERS OF TEAM
     * -------------------------------------------------------------- */
    fun getPlayersOfTeam(teamId: String): List<Player> {
        val url = "https://www.whoscored.com/teams/$teamId/show"
        driver.get(url)

        if (!isTestMode) {
            wait!!.until(ExpectedConditions.presenceOfElementLocated(By.id("team-squad-stats")))
        }

        val rows = driver.findElements(By.cssSelector("#team-squad-stats tbody tr")).toList()
        val players = mutableListOf<Player>()
        val js = driver as JavascriptExecutor

        for (row in rows) {
            try {
                // 1) Obtener el primer TD
                val firstTd = row.findElements(By.tagName("td")).firstOrNull() ?: continue

                // 2) Obtener el <a> dentro del TD
                val nameAnchor = try {
                    firstTd.findElement(By.tagName("a"))
                } catch (_: Exception) {
                    continue
                }

                // 3) Extraer nombre seguro
                val rawName = getCellText(js, nameAnchor)
                val name = rawName.replace(Regex("^\\d+\\s*"), "").trim()
                if (name.isEmpty()) continue

                // 4) Extraer ID WhoScored
                val link = nameAnchor.getAttribute("href") ?: ""
                val whoScoredId = Regex("""/players?/(\d+)/""", RegexOption.IGNORE_CASE)
                    .find(link)?.groupValues?.get(1) ?: "0"

                // Utilidad para celdas que dependen de class
                fun textByClass(css: String): String {
                    val el = row.findElements(By.cssSelector("td.$css")).firstOrNull()
                    return getCellText(js, el)
                }

                val minutes = textByClass("minsPlayed").replace(",", "").toIntOrNull() ?: 0
                val goals = textByClass("goal").toIntOrNull() ?: 0
                val assists = textByClass("assistTotal").toIntOrNull() ?: 0
                val yellow = textByClass("yellowCard").toIntOrNull() ?: 0
                val red = textByClass("redCard").toIntOrNull() ?: 0
                val rating = textByClass("rating").replace(",", ".").toDoubleOrNull()

                // Apps desde la columna 3
                val appsText = row.findElements(By.tagName("td")).getOrNull(3)?.text ?: "0"
                val apps = Regex("""\d+""").find(appsText)?.value?.toInt() ?: 0

                players.add(
                    Player(
                        id = null,
                        teamId = teamId,
                        name = name,
                        matchesPlayed = apps,
                        goals = goals,
                        assists = assists,
                        rating = rating,
                        yellowCards = yellow,
                        redCards = red,
                        minutesPlayed = minutes,
                        whoScoredId = whoScoredId
                    )
                )
            } catch (e: Exception) {
                logger.warn("Error processing row for team {}: {}", teamId, e.message)
            }
        }

        return players
    }

    /** -------------------------------------------------------------
     *  SCRAPE PLAYER HISTORY
     * -------------------------------------------------------------- */
    fun getPlayerHistory(playerId: String, playerName: String): PlayerHistoryDTO? {
        val url = "https://www.whoscored.com/players/$playerId/history/$playerName"
        driver.get(url)

        if (!isTestMode) {
            wait!!.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("#player-table-statistics-body")
                )
            )
        }

        val tbody = driver.findElement(By.cssSelector("#player-table-statistics-body"))
        val rows = tbody.findElements(By.tagName("tr")).toList()
        if (rows.isEmpty()) return null

        val lastRow = rows.last()
        val js = driver as JavascriptExecutor
        val cells = lastRow.findElements(By.tagName("td")).toList()

        if (cells.size < 9) return null

        fun cellText(index: Int): String {
            return getCellText(js, cells.getOrNull(index))
        }

        val appearances = cellText(2).replace(",", "").toIntOrNull() ?: 0
        val minutesPlayed = cellText(3).replace(",", "").toIntOrNull() ?: 0
        val goals = cellText(4).replace(",", "").toIntOrNull() ?: 0
        val assists = cellText(5).replace(",", "").toIntOrNull() ?: 0
        val yellowCards = cellText(6).replace(",", "").toIntOrNull() ?: 0
        val redCards = cellText(7).replace(",", "").toIntOrNull() ?: 0
        val averageRating = cellText(12).replace(",", ".").toDoubleOrNull()

        return PlayerHistoryDTO(
            appearances = appearances,
            goals = goals,
            assists = assists,
            yellowCards = yellowCards,
            redCards = redCards,
            minutesPlayed = minutesPlayed,
            averageRating = averageRating
        )
    }
}
