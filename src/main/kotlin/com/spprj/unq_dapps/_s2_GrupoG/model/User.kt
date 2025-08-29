package com.spprj.unq_dapps._s2_GrupoG.model

import jakarta.persistence.*

@Entity
open class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null,

    open var name: String = ""
)