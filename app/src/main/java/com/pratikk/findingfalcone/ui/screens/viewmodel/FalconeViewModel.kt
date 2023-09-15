package com.pratikk.findingfalcone.ui.screens.viewmodel

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pratikk.findingfalcone.data.core.model.ApiError
import com.pratikk.findingfalcone.data.core.model.onError
import com.pratikk.findingfalcone.data.core.model.onSuccess
import com.pratikk.findingfalcone.data.planets.GetPlanetsService
import com.pratikk.findingfalcone.data.planets.model.Planet
import com.pratikk.findingfalcone.data.vehicles.GetVehiclesService
import com.pratikk.findingfalcone.data.vehicles.model.Vehicle
import com.pratikk.findingfalcone.ui.screens.common.UIError
import com.pratikk.findingfalcone.ui.screens.common.UILoading
import com.pratikk.findingfalcone.ui.screens.common.UIState
import com.pratikk.findingfalcone.ui.screens.common.UISuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FalconeViewModel : ViewModel() {
    private val _vehicle = MutableStateFlow<List<Vehicle>>(listOf())
    private val _selectedVehicle = MutableStateFlow<List<Vehicle>>(listOf())
    val vehicles = _selectedVehicle.asStateFlow()

    private val _planet = MutableStateFlow<List<Planet>>(listOf())

    private val _uiState = MutableStateFlow<UIState>(UILoading)
    val uiState = _uiState.asStateFlow()

    private val _planet1 = MutableStateFlow<List<Planet>>(listOf())
    val planets1 = _planet1.asStateFlow()

    private val _planet2 = MutableStateFlow<List<Planet>>(listOf())
    val planets2 = _planet2.asStateFlow()

    private val _planet3 = MutableStateFlow<List<Planet>>(listOf())
    val planets3 = _planet3.asStateFlow()

    private val _planet4 = MutableStateFlow<List<Planet>>(listOf())
    val planets4 = _planet4.asStateFlow()

    val selectedPlanetMap = mutableStateMapOf<Int, Planet>()
    val searchPlanetMap = mutableStateMapOf<Int, String?>()
    val selectedVehiclesMap = mutableStateMapOf<Int, Vehicle>()
    var totalTime = mutableStateOf<Long>(0L)
    init {
        fetchDetails()
    }

    fun fetchDetails(){
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.emit(UILoading)
            val res = awaitAll(getPlanets(), getVehicles())
            if (res.any { it.isApiError }) {
                _uiState.emit(UIError((res.first { it.isApiError } as ApiError).error))
            }else{
                _uiState.emit(UISuccess)
            }
        }
    }

    private suspend fun getPlanets() = coroutineScope {
        async(this.coroutineContext) {
            GetPlanetsService().getPlanets().onSuccess {
                _planet.emit(it)
                _planet1.emit(it)
                _planet2.emit(it)
                _planet3.emit(it)
                _planet4.emit(it)
            }.onError {

            }
        }
    }

    private suspend fun getVehicles() = coroutineScope {
        async(this.coroutineContext) {
            GetVehiclesService().getVehicles().onSuccess {
                _vehicle.emit(it)
                _selectedVehicle.emit(it)
            }.onError {

            }
        }
    }

    fun searchPlanets1(searchString: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            searchPlanetMap[0] = searchString
            selectedPlanetMap.remove(0)
            selectedVehiclesMap.remove(0)
            refreshRadioButton()
            if (searchString.isNullOrEmpty()) {
                _planet1.emit(_planet.value)
            } else {
                _planet1.emit(_planet.value.filter {
                    it.name.lowercase().contains(searchString.lowercase())
                })
            }
        }
    }

    fun searchPlanets2(searchString: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            searchPlanetMap[1] = searchString
            selectedPlanetMap.remove(1)
            selectedVehiclesMap.remove(1)
            refreshRadioButton()
            if (searchString.isNullOrEmpty()) {
                _planet2.emit(_planet.value)
            } else {
                _planet2.emit(_planet.value.filter {
                    it.name.lowercase().contains(searchString.lowercase())
                })
            }
        }
    }

    fun searchPlanets3(searchString: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            searchPlanetMap[2] = searchString
            selectedPlanetMap.remove(2)
            selectedVehiclesMap.remove(2)
            refreshRadioButton()
            if (searchString.isNullOrEmpty()) {
                _planet3.emit(_planet.value)
            } else {
                _planet3.emit(_planet.value.filter {
                    it.name.lowercase().contains(searchString.lowercase())
                })
            }
        }
    }

    fun searchPlanets4(searchString: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            searchPlanetMap[3] = searchString
            selectedPlanetMap.remove(3)
            selectedVehiclesMap.remove(3)
            refreshRadioButton()
            if (searchString.isNullOrEmpty()) {
                _planet4.emit(_planet.value)
            } else {
                _planet4.emit(_planet.value.filter {
                    it.name.lowercase().contains(searchString.lowercase())
                })
            }
        }
    }

    fun setPlanet(idx: Int, planet: Planet) {
        searchPlanetMap[idx] = planet.name
        selectedPlanetMap[idx] = planet
    }

    fun setVehicle(idx: Int, vehicle: Vehicle) {
        if (selectedVehiclesMap.containsKey(idx)) {
            val veh = selectedVehiclesMap[idx]
            selectedVehiclesMap.remove(idx)
            if(veh?.name != vehicle.name)
                selectedVehiclesMap[idx] = vehicle.copy()
        }
        else
            selectedVehiclesMap[idx] = vehicle.copy()
        refreshRadioButton()
    }
    private fun refreshRadioButton(){
        viewModelScope.launch(Dispatchers.IO) {
            _selectedVehicle.emit(_vehicle.value.map { veh ->
                veh.copy()
                    .apply { totalNo -= selectedVehiclesMap.count { it.value.name == veh.name } }
            })
            totalTime.value = 0
            selectedPlanetMap.forEach { (i, planets) ->
                val veh = selectedVehiclesMap[i]
                if(planets == null || veh == null) {
                    return@forEach
                }
                totalTime.value += (planets.distance.div(veh.speed))
            }
        }
    }
    fun resetInput(){
        viewModelScope.launch(Dispatchers.IO){
            _planet1.emit(buildList { addAll(_planet.value)})
            _planet2.emit(buildList { addAll(_planet.value)})
            _planet3.emit(buildList { addAll(_planet.value)})
            _planet4.emit(buildList { addAll(_planet.value)})
            selectedPlanetMap.clear()
            searchPlanetMap.clear()
            selectedVehiclesMap.clear()
            _selectedVehicle.emit(_vehicle.value.map { veh ->
                veh.copy()
                    .apply { totalNo -= selectedVehiclesMap.count { it.value.name == veh.name } }
            })
            totalTime.value = 0
        }
    }
}
