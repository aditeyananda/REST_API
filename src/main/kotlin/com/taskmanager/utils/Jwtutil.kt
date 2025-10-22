package com.taskmanager.utils

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component
class Jwtutil {

    // Secret key for signing tokens (in production, use environment variable!)
    private val SECRET_KEY = "MyVerySecretKeyThatIsAtLeast256BitsLongForHS256Algorithm"
    private val key: SecretKey = Keys.hmacShaKeyFor(SECRET_KEY.toByteArray())

    // Token validity: 24 hours
    private val EXPIRATION_TIME = 86400000L // 24 hours in milliseconds

    // Generate JWT token for a user
    fun generateToken(email: String): String {
        return Jwts.builder()
            .subject(email)  // Store email in token
            .issuedAt(Date())  // When token was created
            .expiration(Date(System.currentTimeMillis() + EXPIRATION_TIME))  // When it expires
            .signWith(key)  // Sign with secret key
            .compact()
    }

    // Extract email from token
    fun extractEmail(token: String): String {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
            .subject
    }

    // Validate token
    fun isTokenValid(token: String, email: String): Boolean {
        val extractedEmail = extractEmail(token)
        return extractedEmail == email && !isTokenExpired(token)
    }

    // Check if token is expired
    private fun isTokenExpired(token: String): Boolean {
        val expiration = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
            .expiration
        return expiration.before(Date())
    }
}