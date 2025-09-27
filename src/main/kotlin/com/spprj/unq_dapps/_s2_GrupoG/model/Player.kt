package com.spprj.unq_dapps._s2_GrupoG.model

import jakarta.persistence.*

@Entity
@Table(name = "players")
open class Player(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val teamId: String,

    @Column(nullable = false)
    val name: String,

    val matchesPlayed: Int,
    val goals: Int,
    val assists: Int,
    val rating: Double?
)