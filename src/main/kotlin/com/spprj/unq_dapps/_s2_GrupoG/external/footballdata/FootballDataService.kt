package com.spprj.unq_dapps._s2_GrupoG.external.footballdata

import com.spprj.unq_dapps._s2_GrupoG.config.FootballDataApiProperties
import com.spprj.unq_dapps._s2_GrupoG.external.footballdata.dtos.FootballDataMatchDetailResponse
import com.spprj.unq_dapps._s2_GrupoG.external.footballdata.dtos.FootballDataMatchesResponse
import com.spprj.unq_dapps._s2_GrupoG.external.footballdata.dtos.FootballDataTeamResponse
import com.spprj.unq_dapps._s2_GrupoG.model.Player
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.net.URI

@Service
class FootballDataService(
    private val restTemplate: RestTemplate,
    private val props: FootballDataApiProperties
) {

    private val X_AUTH_TOKEN_HEADER = "X-Auth-Token"

    fun playersOfTeam(teamId: Long): List<Player> {
        val uri = URI.create("${props.baseUrl}/teams/$teamId")

        val headers = HttpHeaders().apply {
            set(X_AUTH_TOKEN_HEADER, props.token)
        }
        val request = RequestEntity<Any>(headers, HttpMethod.GET, uri)

        val response = restTemplate.exchange(request, FootballDataTeamResponse::class.java)

        val body = response.body ?: throw IllegalStateException("No se pudo traer datos del equipo $teamId")

        return body.squad.map { squadPlayer ->
            Player(
                name = squadPlayer.name,
                matchesPlayed = squadPlayer.stats?.matchesOnPitch ?: 0,
                goals = squadPlayer.stats?.goals ?: 0,
                assists = squadPlayer.stats?.assists ?: 0,
                rating = null // quedará null hasta scraping de WhoScored
            )
        }
    }

    fun getRecentMatches(teamId: Long, limit: Int = 10): List<Long> {
        val uri = URI.create("${props.baseUrl}/teams/$teamId/matches?status=FINISHED&limit=$limit")
        val headers = HttpHeaders().apply { set(X_AUTH_TOKEN_HEADER, props.token) }
        val request = RequestEntity<Any>(headers, HttpMethod.GET, uri)
        val response = restTemplate.exchange(request, FootballDataMatchesResponse::class.java)
        val body = response.body ?: return emptyList()
        return body.matches.map { it.id }
    }

    fun getMatch(matchId: Long): FootballDataMatchDetailResponse {
        val uri = URI.create("${props.baseUrl}/matches/$matchId")
        val headers = HttpHeaders().apply { set(X_AUTH_TOKEN_HEADER, props.token) }
        val request = RequestEntity<Any>(headers, HttpMethod.GET, uri)
        val response = restTemplate.exchange(request, FootballDataMatchDetailResponse::class.java)
        return response.body ?: FootballDataMatchDetailResponse()
    }

}
