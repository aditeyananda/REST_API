package com.taskmanager

import com.taskmanager.services.AuthService
import com.taskmanager.utils.Jwtutil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtUtil: Jwtutil,
    private val authService: AuthService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // Get Authorization header
        val authHeader = request.getHeader("Authorization")

        // Check if header exists and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            // Extract token (remove "Bearer " prefix)
            val token = authHeader.substring(7)

            // Extract email from token
            val email = jwtUtil.extractEmail(token)

            // If email exists and user not already authenticated
            if (SecurityContextHolder.getContext().authentication == null) {
                // Get user from database
                val user = authService.getUserByEmail(email)

                // Validate token
                if (jwtUtil.isTokenValid(token, user.email)) {
                    // Create authentication object
                    val authToken = UsernamePasswordAuthenticationToken(
                        user.email,
                        null,
                        emptyList()
                    )
                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)

                    // Set authentication in security context
                    SecurityContextHolder.getContext().authentication = authToken
                }
            }
        } catch (e: Exception) {
            // Token invalid, do nothing
        }

        // Continue filter chain
        filterChain.doFilter(request, response)
    }
}