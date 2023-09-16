package com.pratikk.findingfalcone.ui.screens.viewmodel

import app.cash.turbine.test
import com.pratikk.findingfalcone.data.core.model.ApiError
import com.pratikk.findingfalcone.data.core.model.ApiSuccess
import com.pratikk.findingfalcone.data.planets.GetPlanetsRepository
import com.pratikk.findingfalcone.data.planets.model.Planet
import com.pratikk.findingfalcone.data.vehicles.GetVehiclesRepository
import com.pratikk.findingfalcone.data.vehicles.model.Vehicle
import com.pratikk.findingfalcone.ui.screens.common.UIError
import com.pratikk.findingfalcone.ui.screens.common.UILoading
import com.pratikk.findingfalcone.ui.screens.common.UISuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class FalconeViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @Mock
    lateinit var getPlanetsRepository: GetPlanetsRepository
    @Mock
    lateinit var getVehiclesRepository: GetVehiclesRepository

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        Mockito.reset(getPlanetsRepository)
        Mockito.reset(getVehiclesRepository)
    }

    @Test
    fun `If vehicles and planets is success`() = runTest{
        val viewModel = FalconeViewModel(getPlanetsRepository, getVehiclesRepository)
        Mockito.`when`(getPlanetsRepository.getPlanets()).thenReturn(ApiSuccess(listOf(
            Planet("Donlon",100),
            Planet("Enchai",200),
            Planet("Jebing",300),
            Planet("Sapir",400),
            Planet("Lerbin",500),
            Planet("Pingasor",600),
        )))

        Mockito.`when`(getVehiclesRepository.getVehicles()).thenReturn(ApiSuccess(listOf(
            Vehicle("Space pod",2,200,2),
            Vehicle("Space rocket",1,300,4),
            Vehicle("Space shuttle",1,400,5),
            Vehicle("Space ship",2,600,10),
        )))
        val job = launch {
            viewModel.uiState.test {
                awaitItem().apply {
                    if(this is UILoading){
                        assertTrue(awaitItem() is UISuccess)
                    }else{
                        assertTrue(this is UISuccess)
                    }
                }
            }
        }
        viewModel.fetchDetails()
        job.join()
        job.cancel()
    }
    @Test
    fun `If planets is success and vehicle fails`() = runTest{
        val viewModel = FalconeViewModel(getPlanetsRepository, getVehiclesRepository)
        Mockito.`when`(getPlanetsRepository.getPlanets()).thenReturn(ApiSuccess(listOf(
            Planet("Donlon",100),
            Planet("Enchai",200),
            Planet("Jebing",300),
            Planet("Sapir",400),
            Planet("Lerbin",500),
            Planet("Pingasor",600),
        )))
        Mockito.`when`(getVehiclesRepository.getVehicles()).thenReturn(ApiError("Some error"))

        val job = launch {
            viewModel.uiState.test {
                awaitItem().apply {
                    if(this is UILoading){
                        assertTrue(awaitItem() is UIError)
                    }else{
                        assertTrue(this is UIError)
                    }
                }
            }
        }
        viewModel.fetchDetails()
        job.join()
        job.cancel()
    }

    @Test
    fun `If vehicles is success and planet fails`() = runTest{
        val viewModel = FalconeViewModel(getPlanetsRepository, getVehiclesRepository)
        Mockito.`when`(getPlanetsRepository.getPlanets()).thenReturn(ApiError("Some error"))

        Mockito.`when`(getVehiclesRepository.getVehicles()).thenReturn(ApiSuccess(listOf(
            Vehicle("Space pod",2,200,2),
            Vehicle("Space rocket",1,300,4),
            Vehicle("Space shuttle",1,400,5),
            Vehicle("Space ship",2,600,10),
        )))
        val job = launch {
            viewModel.uiState.test {
                awaitItem().apply {
                    if(this is UILoading){
                        assertTrue(awaitItem() is UIError)
                    }else{
                        assertTrue(this is UIError)
                    }
                }
            }
        }
        viewModel.fetchDetails()
        job.join()
        job.cancel()
    }

    @Test
    fun `Success on Retry If vehicles is success at first and planet fails`() = runTest{
        val viewModel = FalconeViewModel(getPlanetsRepository, getVehiclesRepository)
        Mockito.`when`(getPlanetsRepository.getPlanets()).thenReturn(ApiError("Some error"))

        Mockito.`when`(getVehiclesRepository.getVehicles()).thenReturn(ApiSuccess(listOf(
            Vehicle("Space pod",2,200,2),
            Vehicle("Space rocket",1,300,4),
            Vehicle("Space shuttle",1,400,5),
            Vehicle("Space ship",2,600,10),
        )))
        val jobA = launch {
            viewModel.uiState.test {
                awaitItem().apply {
                    if(this is UILoading){
                        assertTrue(awaitItem() is UIError)
                    }else{
                        assertTrue(this is UIError)
                    }
                }
            }
        }
        viewModel.fetchDetails()
        jobA.join()
        jobA.cancel()
        Mockito.`when`(getPlanetsRepository.getPlanets()).thenReturn(ApiSuccess(listOf(
            Planet("Donlon",100),
            Planet("Enchai",200),
            Planet("Jebing",300),
            Planet("Sapir",400),
            Planet("Lerbin",500),
            Planet("Pingasor",600),
        )))
        assertTrue(viewModel.uiState.value is UIError)
        val jobB = launch {
            viewModel.uiState.test {
                awaitItem().apply {
                    if(this is UILoading){
                        assertTrue(awaitItem() is UISuccess)
                    }else{
                        assertTrue(this is UISuccess)
                    }
                }
            }
        }
        viewModel.fetchDetails()
        jobB.join()
        jobB.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}