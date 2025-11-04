package com.spprj.unq_dapps._s2_GrupoG.external.footballdata

import com.spprj.unq_dapps._s2_GrupoG.external.dto.UpcomingMatchDTO
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class FootballDataService(
    @Value("\${footballdata.api.base-url}") private val apiUrl: String,
    @Value("\${footballdata.api.token}") private val apiKey: String,
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    private val restTemplate: RestTemplate = RestTemplate()
) {

    fun getUpcomingMatches(teamId: String): List<UpcomingMatchDTO> {
        val url = "$apiUrl/teams/$teamId/matches?status=SCHEDULED"

        val headers = HttpHeaders()
        headers.set("X-Auth-Token", apiKey)

        val entity = HttpEntity<String>(headers)

        val response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            Map::class.java
        )

        val matches = response.body?.get("matches") as? List<Map<String, Any>> ?: return emptyList()

        return matches.map {
            UpcomingMatchDTO(
                homeTeam = (it["homeTeam"] as Map<*, *>)["name"].toString(),
                awayTeam = (it["awayTeam"] as Map<*, *>)["name"].toString(),
                matchDate = it["utcDate"].toString()
            )
        }
    }
}
