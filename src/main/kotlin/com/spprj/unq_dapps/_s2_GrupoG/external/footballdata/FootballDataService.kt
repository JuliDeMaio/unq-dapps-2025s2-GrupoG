package com.spprj.unq_dapps._s2_GrupoG.external.footballdata

import com.spprj.unq_dapps._s2_GrupoG.model.Player
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.net.URI

@Service
class FootballDataService(
    private val restTemplate: RestTemplate
) {
    private val baseUrl = "https://api.football-data.org/v4"
    private val token = "e7986576ad454ff186dadd4e06c8700d" // ⚠️ reemplazalo con tu X-Auth-Token
    private val X_AUTH_TOKEN_HEADER = "X-Auth-Token"

    fun playersOfTeam(teamId: Long): List<Player> {
        val uri = URI.create("$baseUrl/teams/$teamId")

        val headers = HttpHeaders().apply {
            set(X_AUTH_TOKEN_HEADER, token)
        }
        val request = RequestEntity<Any>(headers, HttpMethod.GET, uri)

        val response = restTemplate.exchange(request, Map::class.java)
        val body = response.body ?: return emptyList()

        val squad = body["squad"] as? List<Map<String, Any>> ?: return emptyList()

        return squad.map {
            Player(
                name = it["name"] as? String ?: "Unknown",
                matchesPlayed = 0, // Football-Data free no da stats individuales
                goals = 0,
                assists = 0,
                rating = null
            )
        }
    }
}
