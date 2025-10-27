package com.spprj.unq_dapps._s2_GrupoG.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "teams")
open class Team(
    @Id
    @Column(length = 50)
    val id: String,

    @Column(nullable = false, unique = true)
    val name: String,

    @Column
    val rating: Double? = null
)