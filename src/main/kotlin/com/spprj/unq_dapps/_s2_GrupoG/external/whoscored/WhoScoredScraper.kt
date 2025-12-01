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
        driver.get(url)

        if (!isTestMode) {
            wait!!.until(ExpectedConditions.presenceOfElementLocated(By.id("team-squad-stats")))
        }

        val rows = driver.findElements(
            By.cssSelector("#team-squad-stats tbody tr")
        ).toList()

        val players = mutableListOf<Player>()
        val js = driver as JavascriptExecutor

        for (row in rows) {
            try {
                val nameElement = row.findElement(By.cssSelector("td a"))
                val name = (js.executeScript(INNER_TEXT_SCRIPT, nameElement) as String)
                    .trim().replace(Regex("^\\d+\\s*"), "")
                if (name.isBlank()) continue

                val link = nameElement.getAttribute("href")
                val whoScoredId = Regex("""/players?/(\d+)/""", RegexOption.IGNORE_CASE)
                    .find(link)?.groupValues?.get(1) ?: "0"

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

                val appsText = row.findElements(By.tagName("td"))
                    .getOrNull(3)?.text ?: "0"
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
                println("Error processing row: ${e.message}")
            }
        }

        return players
    }


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

        fun cellText(cell: WebElement?) = try {
            (js.executeScript(INNER_TEXT_SCRIPT, cell) as String).trim()
        } catch (_: Exception) {
            ""
        }

        val cells = lastRow.findElements(By.tagName("td")).toList()
        if (cells.size < 9) return null

        val appearances = cellText(cells.getOrNull(2)).replace(",", "").toIntOrNull() ?: 0
        val minutesPlayed = cellText(cells.getOrNull(3)).replace(",", "").toIntOrNull() ?: 0
        val goals = cellText(cells.getOrNull(4)).replace(",", "").toIntOrNull() ?: 0
        val assists = cellText(cells.getOrNull(5)).replace(",", "").toIntOrNull() ?: 0
        val yellowCards = cellText(cells.getOrNull(6)).replace(",", "").toIntOrNull() ?: 0
        val redCards = cellText(cells.getOrNull(7)).replace(",", "").toIntOrNull() ?: 0
        val averageRating = cellText(cells.getOrNull(12)).replace(",", ".").toDoubleOrNull()

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
