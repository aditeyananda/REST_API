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
class UserController(private val userService: UserService) {

    @GetMapping
    fun getAllUsers(): List<User> {
        return userService.getAllUsers()
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): User {
        return userService.getUserById(id)
    }

    @GetMapping("email/{email}")
    fun getUserByEmail(@PathVariable email: String): User {
        return userService.getUserByEmail(email)
    }

    @PostMapping
    fun createUser(@RequestBody request: CreateUserRequest) {
        userService.createUser(name = request.name, email = request.email)
    }

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: Long, @RequestBody request: CreateUserRequest ) {
        userService.updateUser(id = id, name = request.name, email = request.email)
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): Map<String, String>{
        userService.deleteUser(id)
        return mapOf("message" to "User deleted successfully")
    }

    data class CreateUserRequest (
        val name: String,
        val email: String
    )
}