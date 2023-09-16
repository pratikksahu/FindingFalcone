package com.pratikk.findingfalcone.ui.screens.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pratikk.findingfalcone.data.core.FalconeTokenRepository
import com.pratikk.findingfalcone.data.core.model.ApiError
import com.pratikk.findingfalcone.data.core.model.ApiSuccess
import com.pratikk.findingfalcone.data.core.model.onError
import com.pratikk.findingfalcone.data.core.model.onSuccess
import com.pratikk.findingfalcone.data.findFalcone.GetFalconeResultRepository
import com.pratikk.findingfalcone.data.findFalcone.model.FalconeResponse
import com.pratikk.findingfalcone.data.planets.model.Planet
import com.pratikk.findingfalcone.data.vehicles.model.Vehicle
import com.pratikk.findingfalcone.ui.screens.common.UIError
import com.pratikk.findingfalcone.ui.screens.common.UILoading
import com.pratikk.findingfalcone.ui.screens.common.UIState
import com.pratikk.findingfalcone.ui.screens.common.UISuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FalconeResultViewModel(
    private val falconeTokenRepository: FalconeTokenRepository,
    private val getFalconeResultRepository: GetFalconeResultRepository) :
    ViewModel() {
    private val _uiState = MutableStateFlow<UIState>(UILoading)
    val uiState = _uiState.asStateFlow()

    private val _falconeResp = MutableStateFlow<FalconeResponse?>(null)
    val falconeResponse = _falconeResp.asStateFlow()
    var totalTime = mutableStateOf(0L)
    private val _planets = MutableStateFlow<List<Planet>>(listOf())
    private val _vehicles = MutableStateFlow<List<Vehicle>>(listOf())

    fun getFaclonResult(planets: List<Planet>, vehicles: List<Vehicle>) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.emit(UILoading)
            if (_planets.value.isEmpty()) {
                _planets.emit(planets)
                _vehicles.emit(vehicles)
            }
            val token = falconeTokenRepository.getToken()
            if (token is ApiError) {
                _uiState.emit(UIError(token.error))
                return@launch
            }
            getFalconeResultRepository.getFalconeResult(
                planets,
                vehicles,
                (token as ApiSuccess).data
            ).onSuccess {
                _falconeResp.emit(it)
                _uiState.emit(UISuccess)
            }.onError {
                _uiState.emit(UIError(it))
            }
        }
    }

    fun retry() {
        getFaclonResult(_planets.value, _vehicles.value)
    }

    fun setTotalTime(value: Long) {
        totalTime.value = value
    }
}

class FalconeResultViewModelFactory(
    private val falconeTokenRepository: FalconeTokenRepository,
    private val getFalconeResultRepository: GetFalconeResultRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FalconeResultViewModel(falconeTokenRepository,getFalconeResultRepository) as T
    }
}