package com.spprj.unq_dapps._s2_GrupoG.model

import com.spprj.unq_dapps._s2_GrupoG.model.enum.Role
import jakarta.persistence.*

@Entity
open class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 120)
    var name: String = "",

    @Column(nullable = false, length = 180, unique = true)
    var email: String = "",

    @Column(nullable = false)
    var password: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    var role: Role = Role.ADMIN
) {
    protected constructor() : this(null, "", "", "", Role.ADMIN)
}
