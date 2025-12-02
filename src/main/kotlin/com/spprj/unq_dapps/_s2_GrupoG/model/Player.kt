package com.spprj.unq_dapps._s2_GrupoG.model

import jakarta.persistence.*

@Entity
@Table(name = "players")
open class Player(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val teamId: String,

    @Column(nullable = false)
    val name: String,

    @Column(name = "matches_played")
    val matchesPlayed: Int,

    @Column(name = "goals")
    val goals: Int,

    @Column(name = "assists")
    val assists: Int,

    @Column(name = "rating")
    val rating: Double?,

    @Column(name = "yellow_cards")
    val yellowCards: Int,

    @Column(name = "red_cards")
    val redCards: Int,

    @Column(name = "minutes_played")
    val minutesPlayed: Int,

    @Column(name = "whoscored_id")
    val whoScoredId: String
)
