package com.spprj.unq_dapps._s2_GrupoG.unit.model

import com.spprj.unq_dapps._s2_GrupoG.model.Player
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("unitTest")
class PlayerTest {

    @Test
    fun `01 - should create Player with correct values`() {
        val player = Player(
            id = 1L,
            teamId = "26",
            name = "Lionel Messi",
            matchesPlayed = 10,
            goals = 8,
            assists = 5,
            rating = 9.1,
            yellowCards = 2,
            redCards = 0,
            minutesPlayed = 900,
            whoScoredId = "12345"
        )

        assertEquals(1L, player.id)
        assertEquals("26", player.teamId)
        assertEquals("Lionel Messi", player.name)
        assertEquals(10, player.matchesPlayed)
        assertEquals(8, player.goals)
        assertEquals(5, player.assists)
        assertEquals(9.1, player.rating)
        assertEquals(2, player.yellowCards)
        assertEquals(0, player.redCards)
        assertEquals(900, player.minutesPlayed)
        assertEquals("12345", player.whoScoredId)
    }

}
