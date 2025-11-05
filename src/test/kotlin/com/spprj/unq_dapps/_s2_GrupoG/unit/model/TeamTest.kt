package com.spprj.unq_dapps._s2_GrupoG.unit.model

import com.spprj.unq_dapps._s2_GrupoG.model.Team
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("unitTest")
class TeamTest {

    @Test
    fun `01 - should create Team correctly`() {
        val team = Team(id = "26", name = "Liverpool", rating = 8.4)
        assertEquals("26", team.id)
        assertEquals("Liverpool", team.name)
        assertEquals(8.4, team.rating)
    }

    @Test
    fun `02 - should allow null rating`() {
        val team = Team(id = "15", name = "Chelsea", rating = null)
        assertNull(team.rating)
    }
}
