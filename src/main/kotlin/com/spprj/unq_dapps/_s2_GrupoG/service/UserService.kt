package com.spprj.unq_dapps._s2_GrupoG.service

import com.spprj.unq_dapps._s2_GrupoG.model.User

interface UserService {
    fun findAll(): List<User>
    fun findById(id: Long): User?
    fun save(user: User): User
    fun update(id: Long, user: User): User?
    fun delete(id: Long): Boolean
}