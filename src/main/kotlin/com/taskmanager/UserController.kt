package com.taskmanager

import com.taskmanager.ddb.models.User
import com.taskmanager.exceptions.UserNotFoundException
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController() {
    private val users = mutableListOf<User>()
    private var nextId = 1L

    @GetMapping
    fun getAllUsers(): List<User> {
        return users
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): User {
        return users.find { it.id == id } ?: throw UserNotFoundException("User not found for id: $id")
    }

    @GetMapping("/{email}")
    fun getUserByEmail(@PathVariable email: String): User {
        return users.find {it.email == email } ?: throw UserNotFoundException("User not found for email: $email")
    }

    @PostMapping
    fun createUser(@RequestBody request: CreateUserRequest) {
        val user = User(id = nextId++, name = request.name, email = request.email)
        users.add(user)
    }

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: Long, @RequestBody request: CreateUserRequest ) {
        val existingUser = users.find { it.id == id } ?: throw UserNotFoundException("User not found for id: $id")

        val updatedUser = existingUser.copy(name = request.name, email = request.email)

        users.remove(existingUser)
        users.add(updatedUser)
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): Map<String, String>{

        val removed = users.removeIf { it.id == id }
        if (!removed) {
            throw UserNotFoundException("User not found with id: $id")
        }
        return mapOf("message" to "User deleted successfully")
    }

    data class CreateUserRequest (
        val name: String,
        val email: String
    )
}