package com.spprj.unq_dapps._s2_GrupoG.external.whoscored

import com.spprj.unq_dapps._s2_GrupoG.model.Player
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
class WhoScoredScraper {

    private val driver: WebDriver by lazy {
        val options = ChromeOptions().apply {
            addArguments("--disable-blink-features=AutomationControlled")
            addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) " +
                    "Chrome/140.0.7339.208 Safari/537.36")
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

    private val wait = WebDriverWait(driver, Duration.ofSeconds(30))

    private fun getCellText(js: JavascriptExecutor, cell: WebElement?): String {
        if (cell == null) return ""
        return try {
            (js.executeScript("return arguments[0].innerText;", cell) as String).trim()
        } catch (e: Exception) {
            ""
        }
    }

    fun getPlayersOfTeam(teamId: String): List<Player> {
        val url = "https://www.whoscored.com/teams/$teamId/show"
        println("Opening team page: $url")
        driver.get(url)

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("team-squad-stats")))

        val rows: List<WebElement> = wait.until {
            val elements = driver.findElements(By.cssSelector("#team-squad-stats tbody tr"))
            if (elements.isNotEmpty()) elements else null
        }

        val players = mutableListOf<Player>()
        println("Detects roads: ${rows.size}")

        val js = driver as JavascriptExecutor

        for (row in rows) {
            try {
                val cells = row.findElements(By.tagName("td"))
                if (cells.isEmpty()) continue

                val nameElement = cells[0].findElement(By.tagName("a"))
                val playerName = (js.executeScript("return arguments[0].innerText;", nameElement) as String)
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
}
