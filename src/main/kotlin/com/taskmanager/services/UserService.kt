package com.taskmanager.services

import com.taskmanager.ddb.dao.UserRepository
import com.taskmanager.ddb.models.User
import com.taskmanager.exceptions.UserNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService (private val userRepository: UserRepository) {
    fun getAllUsers(): List<User> {
        return userRepository.findAll()
    }

    fun getUserById(id: Long): User {
        return userRepository.findById(id).orElseThrow { UserNotFoundException("User not found with id: $id") }
    }

    fun getUserByEmail(email: String): User {
        return userRepository.findByEmail(email) ?: throw UserNotFoundException("User not found with id: $email")
    }

    fun createUser(name: String, email: String) {
        val user = User(name = name, email = email)
        userRepository.save(user)
    }

    fun updateUser(id: Long, name: String, email: String) {
        val existingUser = getUserById(id)

        val updatedUser = existingUser.copy(name = name, email = email)

        // JPA implements UPDATE when working with existing id so no need to delete and add
        userRepository.save(updatedUser)
    }

    fun deleteUser(id: Long) {
        if (!userRepository.existsById(id)) {
            throw UserNotFoundException("User not found with id: $id")
        }

        userRepository.deleteById(id)

    }

}