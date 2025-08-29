package com.spprj.unq_dapps._s2_GrupoG.controller

import com.spprj.unq_dapps._s2_GrupoG.model.User
import com.spprj.unq_dapps._s2_GrupoG.repositories.UserRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user-test")
class UserTestController(private val userRepository: UserRepository) {

    @PostMapping
    fun createUser(@RequestBody user: User): User {
        return userRepository.save(user)
    }

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Long): User? =
        userRepository.findById(id).orElse(null)
}