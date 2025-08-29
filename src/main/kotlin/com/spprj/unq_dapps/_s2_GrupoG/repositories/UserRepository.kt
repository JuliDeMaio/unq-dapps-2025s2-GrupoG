package com.spprj.unq_dapps._s2_GrupoG.repositories

import com.spprj.unq_dapps._s2_GrupoG.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long>