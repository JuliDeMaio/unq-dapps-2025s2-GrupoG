package com.spprj.unq_dapps._s2_GrupoG.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
@EnableConfigurationProperties(FootballDataApiProperties::class)
class FootballDataConfig {

    @Bean
    fun restTemplate(): RestTemplate = RestTemplate()
}

@ConfigurationProperties(prefix = "footballdata.api")
data class FootballDataApiProperties(
    var baseUrl: String = "https://api.football-data.org/v4",
    var token: String = ""
)
