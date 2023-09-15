package com.pratikk.findingfalcone.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pratikk.findingfalcone.data.planets.model.Planet
import com.pratikk.findingfalcone.data.vehicles.model.Vehicle
import com.pratikk.findingfalcone.ui.screens.common.BaseExposedDropdown
import com.pratikk.findingfalcone.ui.screens.common.UIError
import com.pratikk.findingfalcone.ui.screens.common.UILoading
import com.pratikk.findingfalcone.ui.screens.common.UIState
import com.pratikk.findingfalcone.ui.screens.common.UISuccess
import com.pratikk.findingfalcone.ui.screens.common.keyboardAsState
import com.pratikk.findingfalcone.ui.screens.viewmodel.FalconeViewModel
import com.pratikk.findingfalcone.ui.screens.viewmodel.MainViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FindFalcone(
    mainViewModel: MainViewModel,
    falconeViewModel: FalconeViewModel,
    findFalcone:() -> Unit = {}
) {
    val vehicles by falconeViewModel.vehicles.collectAsState()
    val destinations1 by falconeViewModel.planets1.collectAsState()
    val destinations2 by falconeViewModel.planets2.collectAsState()
    val destinations3 by falconeViewModel.planets3.collectAsState()
    val destinations4 by falconeViewModel.planets4.collectAsState()
    val selectedPlanetMap = falconeViewModel.selectedPlanetMap
    val searchPlanetMap =
        falconeViewModel.searchPlanetMap
    val selectedVehicleMap = falconeViewModel.selectedVehiclesMap
    val uiState: UIState by falconeViewModel.uiState.collectAsState()
    val localConfiguration = LocalConfiguration.current
    LaunchedEffect(key1 = uiState, block = {
        println(uiState)
        if (uiState.isUIError)
            mainViewModel.snackBarHost.showSnackbar((uiState as UIError).error.toString())
    })
    var composableLoaded by remember {
        mutableStateOf(false)
    }
    val keyboardVisible by keyboardAsState()
    AnimatedVisibility(
        visible = uiState !is UILoading && composableLoaded,
        enter = fadeIn() + scaleIn(
            tween(
                300,
                delayMillis = 150
            )
        ),
        exit = scaleOut(tween(200, delayMillis = 150)) + fadeOut()
    ) {
        if (uiState is UISuccess)
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .animateContentSize()
                ) {
                    stickyHeader {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(color = MaterialTheme.colorScheme.background)
                                .animateContentSize()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(vertical = 10.dp),
                                    text = "Total Time ${falconeViewModel.totalTime.value}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )

                                if (selectedPlanetMap.isNotEmpty())
                                    OutlinedButton(
                                        shape = MaterialTheme.shapes.small,
                                        onClick = {
                                            falconeViewModel.resetInput()
                                        }) {
                                        Text(text = "Reset")
                                    }
                                if (localConfiguration.orientation != Configuration.ORIENTATION_PORTRAIT && !keyboardVisible)
                                    Button(
                                        modifier = Modifier
                                            .padding(horizontal = 8.dp),
                                        enabled = selectedPlanetMap.size == 4 && selectedVehicleMap.size == 4,
                                        shape = MaterialTheme.shapes.small,
                                        onClick = {
                                            //Call Api
                                            findFalcone()
                                        }) {
                                        Text(text = "Find Falcone")
                                    }
                            }
                        }
                    }
                    item {
                        val showVehicles by remember(selectedPlanetMap[0]) {
                            derivedStateOf { selectedPlanetMap[0] != null }
                        }
                        val density = LocalDensity.current
                        var itemHeight by remember {
                            mutableStateOf(0)
                        }
                        val maxHeight = remember(itemHeight) {
                            if (itemHeight == 0)
                                return@remember 200.dp

                            with(density) { (itemHeight * 2).toDp() }
                        }
                        BaseExposedDropdown(
                            modifier = Modifier.padding(bottom = 8.dp),
                            list = {
                                destinations1.filter {
                                    !selectedPlanetMap.filterKeys { it != 0 }.map { it.value?.name }.contains(it.name)
                                }
                            },
                            searchParam = searchPlanetMap[0] ?: "",
                            onSearch = {
                                falconeViewModel.searchPlanets1(it)
                            },
                            onClick = {
                                falconeViewModel.setPlanet(0, it)
                            }, hintText = "Destination 1"
                        )
                        if (showVehicles)
                            LazyHorizontalGrid(
                                modifier = Modifier.requiredSizeIn(maxHeight = maxHeight),
                                rows = GridCells.Fixed(2)
                            ) {
                                items(vehicles) {
                                    VehicleRadioButton(
                                        modifier = Modifier
                                            .wrapContentSize()
                                            .onSizeChanged {
                                                itemHeight = it.height
                                            },
                                        planet = selectedPlanetMap[0],
                                        item = it,
                                        selectedVehicle = selectedVehicleMap[0],
                                        onClick = {
                                            falconeViewModel.setVehicle(0, it)
                                        })
                                }
                            }

                    }
                    item {
                        val showVehicles by remember(selectedPlanetMap[1]) {
                            derivedStateOf { selectedPlanetMap[1] != null}
                        }
                        val density = LocalDensity.current
                        var itemHeight by remember {
                            mutableStateOf(0)
                        }
                        val maxHeight = remember(itemHeight) {
                            if (itemHeight == 0)
                                return@remember 200.dp

                            with(density) { (itemHeight * 2).toDp() }
                        }
                        BaseExposedDropdown(
                            modifier = Modifier.padding(bottom = 8.dp),
                            list = {
                                destinations2.filter {
                                    !selectedPlanetMap.filterKeys { it != 1 }.map { it.value?.name }.contains(it.name)
                                }
                            },
                            searchParam = searchPlanetMap[1] ?: "",
                            onSearch = {
                                falconeViewModel.searchPlanets2(it)
                            },
                            onClick = {
                                falconeViewModel.setPlanet(1, it)
                            }, hintText = "Destination 2"
                        )
                        if (showVehicles)
                            LazyHorizontalGrid(
                                modifier = Modifier.requiredSizeIn(maxHeight = maxHeight),
                                rows = GridCells.Fixed(2)
                            ) {
                                items(vehicles) {
                                    VehicleRadioButton(
                                        modifier = Modifier
                                            .wrapContentSize()
                                            .onSizeChanged {
                                                itemHeight = it.height
                                            },
                                        planet = selectedPlanetMap[1],
                                        item = it,
                                        selectedVehicle = selectedVehicleMap[1],
                                        onClick = {
                                            falconeViewModel.setVehicle(1, it)
                                        })
                                }
                            }
                    }
                    item {
                        val showVehicles by remember(selectedPlanetMap[2]) {
                            derivedStateOf { selectedPlanetMap[2] != null }
                        }
                        val density = LocalDensity.current
                        var itemHeight by remember {
                            mutableStateOf(0)
                        }
                        val maxHeight = remember(itemHeight) {
                            if (itemHeight == 0)
                                return@remember 200.dp

                            with(density) { (itemHeight * 2).toDp() }
                        }
                        BaseExposedDropdown(
                            modifier = Modifier.padding(bottom = 8.dp),
                            list = {
                                destinations3.filter {
                                    !selectedPlanetMap.filterKeys { it != 2 }.map { it.value?.name }.contains(it.name)
                                }
                            },
                            searchParam = searchPlanetMap[2] ?: "",
                            onSearch = {
                                falconeViewModel.searchPlanets3(it)
                            },
                            onClick = {
                                falconeViewModel.setPlanet(2, it)
                            }, hintText = "Destination 3"
                        )
                        if (showVehicles)
                            LazyHorizontalGrid(
                                modifier = Modifier.requiredSizeIn(maxHeight = maxHeight),
                                rows = GridCells.Fixed(2)
                            ) {
                                items(vehicles) {
                                    VehicleRadioButton(
                                        modifier = Modifier
                                            .wrapContentSize()
                                            .onSizeChanged {
                                                itemHeight = it.height
                                            },
                                        planet = selectedPlanetMap[2],
                                        item = it,
                                        selectedVehicle = selectedVehicleMap[2],
                                        onClick = {
                                            falconeViewModel.setVehicle(2, it)
                                        })
                                }
                            }
                    }
                    item {
                        val showVehicles by remember(selectedPlanetMap[3]) {
                            derivedStateOf { selectedPlanetMap[3] != null }
                        }
                        val density = LocalDensity.current
                        var itemHeight by remember {
                            mutableStateOf(0)
                        }
                        val maxHeight = remember(itemHeight) {
                            if (itemHeight == 0)
                                return@remember 200.dp

                            with(density) { (itemHeight * 2).toDp() }
                        }
                        BaseExposedDropdown(
                            modifier = Modifier.padding(bottom = 8.dp),
                            list = {
                                destinations4.filter {
                                    !selectedPlanetMap.filterKeys { it != 3 }.map { it.value?.name }.contains(it.name)
                                }
                            },
                            searchParam = searchPlanetMap[3] ?: "",
                            onSearch = {
                                falconeViewModel.searchPlanets4(it)
                            },
                            onClick = {
                                falconeViewModel.setPlanet(3, it)
                            }, hintText = "Destination 4"
                        )
                        if (showVehicles)
                            LazyHorizontalGrid(
                                modifier = Modifier.requiredSizeIn(maxHeight = maxHeight),
                                rows = GridCells.Fixed(2)
                            ) {
                                items(vehicles) {
                                    VehicleRadioButton(
                                        modifier = Modifier
                                            .wrapContentSize()
                                            .onSizeChanged {
                                                itemHeight = it.height
                                            },
                                        planet = selectedPlanetMap[3],
                                        item = it,
                                        selectedVehicle = selectedVehicleMap[3],
                                        onClick = {
                                            falconeViewModel.setVehicle(3, it)
                                        })
                                }
                            }
                        if (localConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT)
                            Spacer(modifier = Modifier.height(100.dp))
                    }
                }
                if (localConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT && !keyboardVisible)
                    Button(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(8.dp)
                            .background(color = MaterialTheme.colorScheme.background)
                            .align(Alignment.BottomCenter),
                        enabled = selectedPlanetMap.size == 4 && selectedVehicleMap.size == 4,
                        shape = MaterialTheme.shapes.small,
                        onClick = {
                            //Call Api
                            findFalcone()
                        }) {
                        Text(text = "Find Falcone")
                    }
            }
        if (uiState is UIError) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Button(onClick = {
                    falconeViewModel.fetchDetails()
                }) {
                    Text(text = "Try Again")
                }
            }
        }
    }
    SideEffect {
        composableLoaded = true
    }
}

@Composable
fun VehicleRadioButton(
    modifier: Modifier = Modifier,
    planet:Planet?,
    item: Vehicle,
    selectedVehicle: Vehicle?,
    onClick: () -> Unit
) {
    val isEnabled by remember(planet,selectedVehicle,item) {
        derivedStateOf {
            if(planet == null)
                item.totalNo > 0 || selectedVehicle?.name == item.name
            else if(selectedVehicle != null)
                item.maxDistance >= planet.distance && (item.totalNo > 0 || selectedVehicle.name == item.name)
            else
                item.maxDistance >= planet.distance && item.totalNo > 0
        }
    }
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = selectedVehicle?.name == item.name,
            enabled = isEnabled,
            onClick = onClick
        )
        Column {
            Text(
                text = "${item.name} (${item.totalNo})",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Max Distance: ${item.maxDistance}",
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = "Speed: ${item.speed}",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
