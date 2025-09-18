package com.spprj.unq_dapps._s2_GrupoG.security

import com.spprj.unq_dapps._s2_GrupoG.model.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtTokenProvider {

    private val secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)
    private val expiration: Long = 1000 * 60 * 60 // 1 hora

    fun generateToken(user: User): String {
        return Jwts.builder()
            .setSubject(user.email)
            .claim("role", user.role.name)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + expiration))
            .signWith(secretKey)
            .compact()
    }

    fun extractUsername(token: String): String =
        Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
            .subject

    fun extractRole(token: String): String? =
        Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body["role"] as? String

    fun isTokenValid(token: String, user: User): Boolean {
        val username = extractUsername(token)
        return (username == user.email) && !isTokenExpired(token)
    }

    fun validateToken(token: String, expectedUsername: String): Boolean {
        val extracted = extractUsername(token)
        return extracted == expectedUsername && !isTokenExpired(token)
    }

    private fun isTokenExpired(token: String): Boolean =
        Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
            .expiration.before(Date())
}