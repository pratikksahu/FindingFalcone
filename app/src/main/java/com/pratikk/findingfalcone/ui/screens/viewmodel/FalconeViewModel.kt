package com.pratikk.findingfalcone.ui.screens.viewmodel

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pratikk.findingfalcone.data.core.model.ApiError
import com.pratikk.findingfalcone.data.core.model.onSuccess
import com.pratikk.findingfalcone.data.planets.GetPlanetsRepository
import com.pratikk.findingfalcone.data.planets.model.Planet
import com.pratikk.findingfalcone.data.vehicles.GetVehiclesRepository
import com.pratikk.findingfalcone.data.vehicles.model.Vehicle
import com.pratikk.findingfalcone.ui.screens.common.UIError
import com.pratikk.findingfalcone.ui.screens.common.UILoading
import com.pratikk.findingfalcone.ui.screens.common.UIState
import com.pratikk.findingfalcone.ui.screens.common.UISuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FalconeViewModel constructor(
    private val getPlanetsRepository: GetPlanetsRepository,
    private val getVehiclesRepository: GetVehiclesRepository
) : ViewModel() {
    private val _vehicle = MutableStateFlow<List<Vehicle>>(listOf())
    private val _selectedVehicle = MutableStateFlow<List<Vehicle>>(listOf())
    val vehicles = _selectedVehicle.asStateFlow()

    private val _planet = MutableStateFlow<List<Planet>>(listOf())

    private val _uiState = MutableStateFlow<UIState>(UILoading)
    val uiState = _uiState.asStateFlow()

    private val _filteredPlanets = MutableStateFlow<List<Planet>>(listOf())
    val filteredPlanets = _filteredPlanets.asStateFlow()

    val selectedPlanetMap = mutableStateMapOf<Int, Planet>()
    val selectedVehiclesMap = mutableStateMapOf<Int, Vehicle>()
    var totalTime = mutableStateOf(0L)

    init {
        fetchDetails()
    }
    fun fetchDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.emit(UILoading)
            val planetsRes = getPlanets()
            val vehiclesRes = getVehicles()
            if(planetsRes is ApiError){
                _uiState.emit(UIError((planetsRes.error)))
            }else if(vehiclesRes is ApiError){
                _uiState.emit(UIError((vehiclesRes.error)))
            }else{
                _uiState.emit(UISuccess)
            }
        }
    }

    private suspend fun getPlanets() = coroutineScope {
        return@coroutineScope getPlanetsRepository.getPlanets().onSuccess {
            _planet.emit(it)
            _filteredPlanets.emit(it)
        }
    }

    private suspend fun getVehicles() = coroutineScope {
        return@coroutineScope getVehiclesRepository.getVehicles().onSuccess {
            _vehicle.emit(it)
            _selectedVehicle.emit(it)
        }
    }

    fun searchPlanets(searchString: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            if (searchString.isNullOrEmpty()) {
                _filteredPlanets.emit(_planet.value)
            } else {
                _filteredPlanets.emit(_planet.value.filter {
                    it.name.lowercase().contains(searchString.lowercase())
                })
            }
        }
    }
    fun setPlanet(idx: Int, planet: Planet) {
        selectedPlanetMap[idx] = planet
        selectedVehiclesMap.remove(idx)
        refreshRadioButton()
    }

    fun setVehicle(idx: Int, vehicle: Vehicle) {
        if (selectedVehiclesMap.containsKey(idx)) {
            val veh = selectedVehiclesMap[idx]
            selectedVehiclesMap.remove(idx)
            if (veh?.name != vehicle.name)
                selectedVehiclesMap[idx] = vehicle.copy()
        } else
            selectedVehiclesMap[idx] = vehicle.copy()
        refreshRadioButton()
    }

    private fun refreshRadioButton() {
        viewModelScope.launch(Dispatchers.IO) {
            _selectedVehicle.emit(_vehicle.value.map { veh ->
                veh.copy()
                    .apply { totalNo -= selectedVehiclesMap.count { it.value.name == veh.name } }
            })
            totalTime.value = 0
            selectedPlanetMap.forEach { (i, planets) ->
                val veh = selectedVehiclesMap[i] ?: return@forEach
                totalTime.value += (planets.distance.div(veh.speed))
            }
        }
    }

    fun resetInput() {
        viewModelScope.launch(Dispatchers.IO) {
            _filteredPlanets.emit(buildList { addAll(_planet.value) })
            selectedPlanetMap.clear()
            selectedVehiclesMap.clear()
            _selectedVehicle.emit(_vehicle.value.map { veh ->
                veh.copy()
                    .apply { totalNo -= selectedVehiclesMap.count { it.value.name == veh.name } }
            })
            totalTime.value = 0
        }
    }
}

class FalconeViewModelFactory(private val getPlanetsRepository: GetPlanetsRepository,
                              private val getVehiclesRepository: GetVehiclesRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FalconeViewModel(getPlanetsRepository, getVehiclesRepository) as T
    }
}
