package com.spprj.unq_dapps._s2_GrupoG.model

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "user_queries")
open class UserQueryLog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(nullable = false)
    val endpoint: String,

    @Column(nullable = false)
    val method: String,

    @Column(name = "request_body", columnDefinition = "TEXT")
    val requestBody: String? = null,

    @Column(name = "response_body", columnDefinition = "TEXT")
    val responseBody: String? = null,

    @Column(name = "query_date", nullable = false)
    val queryDate: LocalDate,

    @Column(name = "query_timestamp", nullable = false)
    val timestamp: LocalDateTime,

    @Column(name = "query_pathParams", columnDefinition = "TEXT")
    var pathParams: String? = null,

    @Column(name = "query_queryParams", columnDefinition = "TEXT")
    var queryParams: String? = null
)
