package com.spprj.unq_dapps._s2_GrupoG.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SecurityConfig(
    private val customUserDetailsService: CustomUserDetailsService,
    private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager =
        config.authenticationManager

    @Bean
    fun filterChain(http: HttpSecurity, jwtAuthenticationFilter: JwtAuthenticationFilter): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .exceptionHandling { it.authenticationEntryPoint(jwtAuthenticationEntryPoint) }
            .authorizeHttpRequests {
                it
                    .requestMatchers("/auth/**").permitAll()
                    .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                    ).permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}