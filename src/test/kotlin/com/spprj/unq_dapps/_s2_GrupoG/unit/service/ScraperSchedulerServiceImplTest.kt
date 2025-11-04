package com.spprj.unq_dapps._s2_GrupoG.unit.service

import com.spprj.unq_dapps._s2_GrupoG.model.Team
import com.spprj.unq_dapps._s2_GrupoG.repositories.TeamRepository
import com.spprj.unq_dapps._s2_GrupoG.service.impl.PlayerServiceImpl
import com.spprj.unq_dapps._s2_GrupoG.service.impl.ScraperSchedulerServiceImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class ScraperSchedulerServiceImplTest {

    @Mock
    lateinit var playerService: PlayerServiceImpl

    @Mock
    lateinit var teamRepository: TeamRepository

    @InjectMocks
    lateinit var scheduler: ScraperSchedulerServiceImpl

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `01 - should call playerService for each team`() {
        val teams = listOf(
            Team(id = "1", name = "Boca Juniors", rating = 7.5),
            Team(id = "2", name = "River Plate", rating = 8.0)
        )
        `when`(teamRepository.findAll()).thenReturn(teams)

        scheduler.scheduledPopulate()

        verify(playerService, times(1)).populateDataBaseFromScrapperService("1")
        verify(playerService, times(1)).populateDataBaseFromScrapperService("2")
        verify(teamRepository, times(1)).findAll()
    }

    @Test
    fun `02 - should handle exception during scraping`() {
        val teams = listOf(
            Team(id = "1", name = "Independiente", rating = 6.8),
            Team(id = "2", name = "Racing", rating = 7.1)
        )
        `when`(teamRepository.findAll()).thenReturn(teams)
        doThrow(RuntimeException("Scraping failed"))
            .`when`(playerService).populateDataBaseFromScrapperService("1")

        scheduler.scheduledPopulate()

        verify(playerService, times(1)).populateDataBaseFromScrapperService("1")
        verify(playerService, times(1)).populateDataBaseFromScrapperService("2")
    }
}
