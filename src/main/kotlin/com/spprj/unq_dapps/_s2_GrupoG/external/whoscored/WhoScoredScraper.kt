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

    private val playerUrlMap = mapOf(
        "Erling Haaland" to "https://www.whoscored.com/players/315227/show/erling-haaland",
        "Mohamed Salah" to "https://www.whoscored.com/players/108226/show/mohamed-salah",
        "Phil Foden" to "https://www.whoscored.com/players/331254/show/phil-foden",
        "Rúben Dias" to "https://www.whoscored.com/players/313171/show/r%C3%BAben-dias"
    )

    private val driver: WebDriver by lazy {
        val options = ChromeOptions().apply {
            addArguments("--disable-blink-features=AutomationControlled")
            addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) " +
                    "Chrome/140.0.7339.208 Safari/537.36")
            addArguments("--headless=new") // probá también sin headless si sigue bloqueando
            addArguments("--no-sandbox")
            addArguments("--disable-dev-shm-usage")
            addArguments("--disable-extensions")
            addArguments("--remote-allow-origins=*")
            addArguments("--disable-gpu")
            addArguments("--disable-browser-side-navigation")
            addArguments("--dns-prefetch-disable")
            addArguments("--window-size=1920,1080")
            addArguments("--user-data-dir=${System.getProperty("java.io.tmpdir")}/chrome-data-${System.currentTimeMillis()}")
        }
        ChromeDriver(options)
    }

    override fun getStats(teamName: String, playerName: String): PlayerStatsData? {
        val url = playerUrlMap[playerName] ?: return null
        return scrapeStats(url)
    }

    private fun scrapeStats(url: String): PlayerStatsData? {
        return try {
            driver.get(url)
            println(driver.pageSource)
            // Esperar a que aparezca la tabla de stats
            val wait = WebDriverWait(driver, Duration.ofSeconds(10))
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("player-table-statistics-body")))

            val table = driver.findElement(By.cssSelector("table tbody"))
            val firstRow = table.findElements(By.tagName("tr")).firstOrNull() ?: return null
            val cells = firstRow.findElements(By.tagName("td"))

            val matches = cells.getOrNull(2)?.text?.toIntOrNull() ?: 0
            val goals = cells.getOrNull(3)?.text?.toIntOrNull() ?: 0
            val assists = cells.getOrNull(4)?.text?.toIntOrNull() ?: 0
            val rating = cells.lastOrNull()?.text?.toDoubleOrNull()

            PlayerStatsData(
                matchesPlayed = matches,
                goals = goals,
                assists = assists,
                rating = rating
            )
        } catch (e: Exception) {
            println("Error con Selenium en $url → ${e.message}")
            null
        }
    }
}
