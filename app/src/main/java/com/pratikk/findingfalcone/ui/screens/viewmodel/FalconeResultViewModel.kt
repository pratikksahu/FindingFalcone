package com.pratikk.findingfalcone.ui.screens.viewmodel

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pratikk.findingfalcone.data.core.FalconeTokenHelper
import com.pratikk.findingfalcone.data.core.model.ApiError
import com.pratikk.findingfalcone.data.core.model.ApiSuccess
import com.pratikk.findingfalcone.data.core.model.onError
import com.pratikk.findingfalcone.data.core.model.onSuccess
import com.pratikk.findingfalcone.data.findFalcone.GetFalconeResultService
import com.pratikk.findingfalcone.data.findFalcone.model.FalconeResponse
import com.pratikk.findingfalcone.data.planets.GetPlanetsService
import com.pratikk.findingfalcone.data.planets.model.Planet
import com.pratikk.findingfalcone.data.vehicles.model.Vehicle
import com.pratikk.findingfalcone.ui.screens.common.UIError
import com.pratikk.findingfalcone.ui.screens.common.UILoading
import com.pratikk.findingfalcone.ui.screens.common.UIState
import com.pratikk.findingfalcone.ui.screens.common.UISuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FalconeResultViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UIState>(UILoading)
    val uiState = _uiState.asStateFlow()

    private val _falconeResp = MutableStateFlow<FalconeResponse?>(null)
    val falconeResponse = _falconeResp.asStateFlow()

    fun getFaclonResult(planets: List<Planet>, vehicles: List<Vehicle>){
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.emit(UILoading)
            val token = FalconeTokenHelper().getToken()
            if(token is ApiError){
                _uiState.emit(UIError(token.error))
                return@launch
            }
            GetFalconeResultService().getFalconeResult(planets, vehicles,(token as ApiSuccess).data).onSuccess {
                _falconeResp.emit(it)
                println(it)
                _uiState.emit(UISuccess)
            }.onError {
                _uiState.emit(UIError(it))
            }
        }
    }
}