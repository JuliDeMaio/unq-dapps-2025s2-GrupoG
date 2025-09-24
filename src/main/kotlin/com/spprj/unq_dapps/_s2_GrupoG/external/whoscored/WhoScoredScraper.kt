package com.spprj.unq_dapps._s2_GrupoG.external.whoscored

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class WhoScoredScraper : WhoScoredStatsProvider {

    private val driver: WebDriver by lazy {
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
            addArguments("--disable-browser-side-navigation")
            addArguments("--dns-prefetch-disable")
            addArguments("--window-size=1920,1080")
        }
        ChromeDriver(options)
    }

    fun getAllPlayersStatsFromTeamPage(teamId: String): Map<String, PlayerStatsData> {
        val url = "https://www.whoscored.com/teams/$teamId/show"
        println("🌐 Abriendo URL: $url")
        driver.get(url)

        val wait = WebDriverWait(driver, Duration.ofSeconds(15))
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("team-squad-stats")))

        val playerLinks = driver.findElements(By.cssSelector("#team-squad-stats a.player-link"))

        val result = mutableMapOf<String, PlayerStatsData>()

        for (link in playerLinks) {
            val playerName = link.text.trim()
            val playerUrl = link.getAttribute("href")

            if (playerUrl.isNullOrBlank() || playerName.isBlank()) continue

            println("➡️ Procesando jugador: $playerName ($playerUrl)")
            try {
                driver.get(playerUrl)
                wait.until(ExpectedConditions.presenceOfElementLocated(By.id("player-table-statistics-body")))

                val rows = driver.findElement(By.id("player-table-statistics-body"))
                    .findElements(By.tagName("tr"))
                val totalRow = rows.lastOrNull() ?: continue
                val cells = totalRow.findElements(By.tagName("td"))

                val matches = cells.getOrNull(1)?.text?.toIntOrNull() ?: 0
                val goals = cells.getOrNull(3)?.text?.toIntOrNull() ?: 0
                val assists = cells.getOrNull(4)?.text?.toIntOrNull() ?: 0
                val rating = cells.lastOrNull()?.text?.replace(",", ".")?.toDoubleOrNull()

                result[playerName] = PlayerStatsData(
                    matchesPlayed = matches,
                    goals = goals,
                    assists = assists,
                    rating = rating
                )
                println("✅ Stats $playerName: $matches PJ, $goals G, $assists A, $rating Rating")

            } catch (e: Exception) {
                println("❌ Error procesando $playerName → ${e.message}")
            }
        }

        return result
    }

    // Mantengo la interfaz para no romper otras capas
    override fun getStats(teamName: String, playerName: String): PlayerStatsData? {
        // Ahora simplemente no hace búsqueda, sino lookup en el map cargado por teamId
        // Podés cachear el último resultado si querés.
        return null
    }
}
