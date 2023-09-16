package com.pratikk.findingfalcone.ui.screens.viewmodel

import app.cash.turbine.test
import com.pratikk.findingfalcone.data.core.FalconeTokenRepository
import com.pratikk.findingfalcone.data.core.model.ApiError
import com.pratikk.findingfalcone.data.core.model.ApiSuccess
import com.pratikk.findingfalcone.data.findFalcone.GetFalconeResultRepository
import com.pratikk.findingfalcone.data.findFalcone.model.FalconeResponse
import com.pratikk.findingfalcone.data.planets.model.Planet
import com.pratikk.findingfalcone.data.vehicles.model.Vehicle
import com.pratikk.findingfalcone.ui.screens.common.UIError
import com.pratikk.findingfalcone.ui.screens.common.UILoading
import com.pratikk.findingfalcone.ui.screens.common.UISuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class FalconeResultViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    lateinit var falconeTokenRepository: FalconeTokenRepository

    @Mock
    lateinit var getFalconeResultRepository: GetFalconeResultRepository
    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        Mockito.reset(falconeTokenRepository)
        Mockito.reset(getFalconeResultRepository)
    }

    @Test
    fun `Get error if fail to fetch token`() = runTest {
        // Create a ViewModel with the mocked repositories
        val viewModel = FalconeResultViewModel(falconeTokenRepository, getFalconeResultRepository)

        // Mock data for planets and vehicles
        val planets = listOf<Planet>()
        val vehicles = listOf<Vehicle>()

        // Step 1: Mock getToken to return ApiError
        Mockito.`when`(falconeTokenRepository.getToken()).thenReturn(ApiError("token error"))

        // Call the function you want to test
        viewModel.getFaclonResult(planets, vehicles)
        viewModel.uiState.test {
            awaitItem().apply {
                if(this is UILoading){
                    assertTrue(awaitItem() is UIError)
                }else{
                    assertTrue(this is UIError)
                }
            }
        }

        assertNull(viewModel.falconeResponse.value)
    }

    @Test
    fun `Get success if token is not empty`() = runTest {
        // Create a ViewModel with the mocked repositories
        val viewModel = FalconeResultViewModel(falconeTokenRepository, getFalconeResultRepository)

        // Mock data for planets and vehicles
        val planets = listOf(
            Planet("Donlon",100),
            Planet("Enchai",200),
            Planet("Jebing",300),
            Planet("Sapir",400),
            Planet("Lerbin",500),
            Planet("Pingasor",600),
        )
        val vehicles = listOf(
            Vehicle("Space pod",2,200,2),
            Vehicle("Space rocket",1,300,4),
            Vehicle("Space shuttle",1,400,5),
            Vehicle("Space ship",2,600,10),
        )

        Mockito.`when`(falconeTokenRepository.getToken()).thenReturn(ApiSuccess("some token"))
        val token = falconeTokenRepository.getToken()

        assertTrue(token is ApiSuccess)
        assertEquals("some token",(token as ApiSuccess).data)

        Mockito.`when`(getFalconeResultRepository.getFalconeResult(
            planets,
            vehicles,
            token.data
        )).thenReturn(ApiSuccess(FalconeResponse(planets[(0..5).random()].name,"success")))

        assertTrue(viewModel.uiState.value == UILoading)

        // Call the function you want to test
        viewModel.getFaclonResult(planets, vehicles)
        viewModel.uiState.test {
            awaitItem().apply {
                if(this is UILoading){
                    assertTrue(awaitItem() is UISuccess)
                }else{
                    assertTrue(this is UISuccess)
                }
            }
        }

        assertNotNull(viewModel.falconeResponse.value)
        assertTrue(planets.map { it.name }.contains(viewModel.falconeResponse.value!!.planetName))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}