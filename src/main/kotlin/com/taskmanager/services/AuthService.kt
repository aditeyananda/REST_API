package com.taskmanager.services

import com.taskmanager.ddb.dao.UserRepository
import com.taskmanager.ddb.models.AuthResponse
import com.taskmanager.ddb.models.LoginRequest
import com.taskmanager.ddb.models.RegisterRequest
import com.taskmanager.ddb.models.User
import com.taskmanager.utils.Jwtutil
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthService(private val userRepository: UserRepository, private val jwtutil: Jwtutil) {

    private val passwordEncoder = BCryptPasswordEncoder()

    fun register(request: RegisterRequest): AuthResponse {
        if (userRepository.findByEmail(request.email) != null) {
            throw IllegalArgumentException("Email already registered")
        }

        val hashedPassword = passwordEncoder.encode(request.password)

        val user = User(name = request.name, email = request.email, password = hashedPassword)

        val savedUser = userRepository.save(user)

        val token = jwtutil.generateToken(savedUser.email)

        return AuthResponse(
            token = token,
            email = savedUser.email,
            name = savedUser.name
        )
    }

    fun login(request: LoginRequest): AuthResponse {
        val user = userRepository.findByEmail(request.email) ?: throw IllegalArgumentException("Invalid email")

        if(!passwordEncoder.matches(request.password, user.password)) {
            throw IllegalArgumentException("Invalid password")
        }

        val token = jwtutil.generateToken(user.email)

        return AuthResponse(
            token = token,
            email = user.email,
            name = user.name
        )
    }

    // Get user by email
    fun getUserByEmail(email: String): User {
        return userRepository.findByEmail(email)
            ?: throw IllegalArgumentException("User not found")
    }

}