package com.spprj.unq_dapps._s2_GrupoG.security
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUtil {
    private val key = Keys.secretKeyFor(SignatureAlgorithm.HS256)

    fun generateToken(username: String): String {
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hora
            .signWith(key)
            .compact()
    }

    fun extractUsername(token: String): String =
        Jwts.parserBuilder().setSigningKey(key).build()
            .parseClaimsJws(token).body.subject

    fun validateToken(token: String, username: String): Boolean {
        val extracted = extractUsername(token)
        return extracted == username && !isTokenExpired(token)
    }

    private fun isTokenExpired(token: String): Boolean =
        Jwts.parserBuilder().setSigningKey(key).build()
            .parseClaimsJws(token).body.expiration.before(Date())
}
