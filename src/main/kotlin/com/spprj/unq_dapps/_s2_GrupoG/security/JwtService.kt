package com.spprj.unq_dapps._s2_GrupoG.security

import com.spprj.unq_dapps._s2_GrupoG.model.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService {

    private val secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256) // clave secreta segura
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

    fun extractUsername(token: String): String {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
            .subject
    }

    fun isTokenValid(token: String, user: User): Boolean {
        val username = extractUsername(token)
        return (username == user.email) && !isTokenExpired(token)
    }

    private fun isTokenExpired(token: String): Boolean {
        val expiration = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
            .expiration
        return expiration.before(Date())
    }
}