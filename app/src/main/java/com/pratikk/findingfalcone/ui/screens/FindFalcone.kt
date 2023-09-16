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
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.pratikk.findingfalcone.data.planets.model.Planet
import com.pratikk.findingfalcone.data.vehicles.model.Vehicle
import com.pratikk.findingfalcone.ui.screens.common.PlanetSelectionBottomSheet
import com.pratikk.findingfalcone.ui.screens.common.UIError
import com.pratikk.findingfalcone.ui.screens.common.UILoading
import com.pratikk.findingfalcone.ui.screens.common.UIState
import com.pratikk.findingfalcone.ui.screens.common.UISuccess
import com.pratikk.findingfalcone.ui.screens.common.keyboardAsState
import com.pratikk.findingfalcone.ui.screens.viewmodel.FalconeViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FindFalcone(
    snackbarHostState:SnackbarHostState,
    falconeViewModel: FalconeViewModel,
    findFalcone: () -> Unit = {}
) {
    val vehicles by falconeViewModel.vehicles.collectAsState()
    val destinations1 by falconeViewModel.planets1.collectAsState()
    val destinations2 by falconeViewModel.planets2.collectAsState()
    val destinations3 by falconeViewModel.planets3.collectAsState()
    val destinations4 by falconeViewModel.planets4.collectAsState()
    val selectedPlanetMap = falconeViewModel.selectedPlanetMap
    val selectedVehicleMap = falconeViewModel.selectedVehiclesMap
    val uiState: UIState by falconeViewModel.uiState.collectAsState()
    val localConfiguration = LocalConfiguration.current
    LaunchedEffect(key1 = uiState, block = {
        if (uiState.isUIError)
            snackbarHostState.showSnackbar((uiState as UIError).error.toString())
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
                    if (localConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT)
                        stickyHeader {
                            val density = LocalDensity.current
                            var itemHeight by remember {
                                mutableStateOf(0)
                            }
                            val maxHeight = remember(itemHeight) {
                                if (itemHeight == 0)
                                    return@remember 200.dp

                                with(density) { (itemHeight * 2).toDp() }
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(color = MaterialTheme.colorScheme.background)
                                    .animateContentSize()
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 10.dp),
                                        text = "Available Crafts 🛸",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center
                                    )
                                    AvailableVehicles(
                                        modifier = Modifier.requiredSizeIn(maxHeight = maxHeight),
                                        vehicles = vehicles,
                                        onSizeChanged = {
                                            itemHeight = it.height
                                        })
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(vertical = 20.dp),
                                            text = "Total Time : ${falconeViewModel.totalTime.value} ${if(falconeViewModel.totalTime.value != 0L) "hours" else ""}",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Medium
                                        )

                                        if (selectedPlanetMap.isNotEmpty())
                                            TextButton(
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
                                                Text(text = "Find Falcone 🚀")
                                            }
                                    }
                                    Text(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 10.dp),
                                        text = "Planetary Exploration Choices 🪐",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    else
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(color = MaterialTheme.colorScheme.background)
                                    .animateContentSize()
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 10.dp),
                                        text = "Available Crafts 🛸",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center
                                    )
                                    AvailableVehicles(
                                        modifier = Modifier,
                                        vehicles = vehicles,
                                        onSizeChanged = {
                                        })
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(vertical = 20.dp),
                                            text = "Total Time : ${falconeViewModel.totalTime.value}",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Medium
                                        )

                                        if (selectedPlanetMap.isNotEmpty())
                                            TextButton(
                                                shape = MaterialTheme.shapes.small,
                                                onClick = {
                                                    falconeViewModel.resetInput()
                                                }) {
                                                Text(text = "Reset")
                                            }
                                        if (!keyboardVisible)
                                            Button(
                                                modifier = Modifier
                                                    .padding(horizontal = 8.dp),
                                                enabled = selectedPlanetMap.size == 4 && selectedVehicleMap.size == 4,
                                                shape = MaterialTheme.shapes.small,
                                                onClick = {
                                                    //Call Api
                                                    findFalcone()
                                                }) {
                                                Text(text = "Find Falcone 🚀")
                                            }
                                    }
                                    Text(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 10.dp),
                                        text = "Planetary Exploration Choices 🪐",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    item {
                        DestinationItem(
                            selectedPlanetMap = selectedPlanetMap,
                            selectedVehicleMap = selectedVehicleMap,
                            vehicles = vehicles,
                            destinations = destinations1,
                            destinationIdx = 0,
                            onSearch = {
                                falconeViewModel.searchPlanets1(it)
                            }, onPlanetClick = {
                                falconeViewModel.setPlanet(0, it)
                            }, onVehicleClick = {
                                falconeViewModel.setVehicle(0, it)
                            }
                        )
                    }
                    item {
                        DestinationItem(
                            selectedPlanetMap = selectedPlanetMap,
                            selectedVehicleMap = selectedVehicleMap,
                            vehicles = vehicles,
                            destinations = destinations2,
                            destinationIdx = 1,
                            onSearch = {
                                falconeViewModel.searchPlanets2(it)
                            }, onPlanetClick = {
                                falconeViewModel.setPlanet(1, it)
                            }, onVehicleClick = {
                                falconeViewModel.setVehicle(1, it)
                            }
                        )
                    }
                    item {
                        DestinationItem(
                            selectedPlanetMap = selectedPlanetMap,
                            selectedVehicleMap = selectedVehicleMap,
                            vehicles = vehicles,
                            destinations = destinations3,
                            destinationIdx = 2,
                            onSearch = {
                                falconeViewModel.searchPlanets3(it)
                            }, onPlanetClick = {
                                falconeViewModel.setPlanet(2, it)
                            }, onVehicleClick = {
                                falconeViewModel.setVehicle(2, it)
                            }
                        )
                    }
                    item {
                        DestinationItem(
                            selectedPlanetMap = selectedPlanetMap,
                            selectedVehicleMap = selectedVehicleMap,
                            vehicles = vehicles,
                            destinations = destinations4,
                            destinationIdx = 3,
                            onSearch = {
                                falconeViewModel.searchPlanets4(it)
                            }, onPlanetClick = {
                                falconeViewModel.setPlanet(3, it)
                            }, onVehicleClick = {
                                falconeViewModel.setVehicle(3, it)
                            }
                        )
                        if (localConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT)
                            Spacer(modifier = Modifier.height(100.dp))
                    }
                }
                if (localConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT && !keyboardVisible) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .clip(MaterialTheme.shapes.small)
                            .align(Alignment.BottomCenter),
                        enabled = selectedPlanetMap.size == 4 && selectedVehicleMap.size == 4,
                        shape = MaterialTheme.shapes.small,
                        onClick = {
                            //Call Api
                            findFalcone()
                        }) {
                        Text(text = "Find Falcone 🚀")
                    }
                }
            }
        if (uiState is UIError) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Uh-oh! It seems Falcone's cunning has disrupted our connection. King Shan's radar is offline, but we'll keep searching!",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        shape = MaterialTheme.shapes.small,
                        onClick = {
                            falconeViewModel.fetchDetails()
                        }) {
                        Text(text = "Try Again")
                    }
                }
            }
        }
    }
    SideEffect {
        composableLoaded = true
    }
}

@Composable
fun DestinationItem(
    selectedPlanetMap: SnapshotStateMap<Int, Planet>,
    selectedVehicleMap: SnapshotStateMap<Int, Vehicle>,
    vehicles:List<Vehicle>,
    destinations: List<Planet>,
    destinationIdx:Int,
    onSearch:(String) -> Unit,
    onPlanetClick: (Planet) -> Unit,
    onVehicleClick: (Vehicle) -> Unit
) {
    val showVehicles by remember(selectedPlanetMap[destinationIdx]) {
        derivedStateOf { selectedPlanetMap[destinationIdx] != null }
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
//                        BaseExposedDropdown(
//                            modifier = Modifier.padding(bottom = 8.dp),
//                            selectedItem = selectedPlanetMap[destinationIdx],
//                            list = {
//                                destinations1.filter {
//                                    !selectedPlanetMap.filterKeys { it != destinationIdx }.map { it.value?.name }
//                                        .contains(it.name)
//                                }
//                            },
//                            onSearch = {
//                                falconeViewModel.searchPlanets1(it)
//                            },
//                            onClick = {
//                                falconeViewModel.setPlanet(0, it)
//                            }, hintText = hintText
//                        )
    PlanetSelectionBottomSheet(
        selectedItem = selectedPlanetMap[destinationIdx],
        list = {
            destinations.filter {
                !selectedPlanetMap.filterKeys { it != destinationIdx }.map { it.value.name }
                    .contains(it.name)
            }
        },
        onSearch = onSearch,
        onClick = onPlanetClick, hintText = "Destination ${destinationIdx + 1}"
    )
    if (showVehicles)
        VehicleOptions(
            modifier = Modifier.requiredSizeIn(maxHeight = maxHeight),
            idx = destinationIdx,
            vehicles = vehicles,
            selectedVehicleMap = selectedVehicleMap,
            selectedPlanetMap = selectedPlanetMap,
            onSizeChanged = {
                itemHeight = it.height
            },
            onClick = onVehicleClick
        )
}

@Composable
fun VehicleOptions(
    modifier: Modifier,
    idx: Int,
    vehicles: List<Vehicle>,
    selectedVehicleMap: SnapshotStateMap<Int, Vehicle>,
    selectedPlanetMap: SnapshotStateMap<Int, Planet>,
    onSizeChanged: (IntSize) -> Unit,
    onClick: (Vehicle) -> Unit
) {
    val localConfiguration = LocalConfiguration.current
    if (localConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT)
        LazyHorizontalGrid(
            modifier = modifier,
            rows = GridCells.Fixed(2)
        ) {
            items(vehicles) {
                VehicleRadioButton(
                    modifier = Modifier
                        .wrapContentSize()
                        .onSizeChanged {
                            onSizeChanged(it)
                        },
                    planet = selectedPlanetMap[idx],
                    item = it,
                    selectedVehicle = selectedVehicleMap[idx],
                    onClick = { onClick(it) })
            }
        }
    else
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            vehicles.forEach {
                VehicleRadioButton(
                    modifier = Modifier
                        .weight(1f),
                    planet = selectedPlanetMap[idx],
                    item = it,
                    selectedVehicle = selectedVehicleMap[idx],
                    onClick = { onClick(it) })
            }

        }
}

@Composable
fun VehicleRadioButton(
    modifier: Modifier = Modifier,
    planet: Planet?,
    item: Vehicle,
    selectedVehicle: Vehicle?,
    onClick: () -> Unit
) {
    val isEnabled by remember(planet, selectedVehicle, item) {
        derivedStateOf {
            if (planet == null)
                item.totalNo > 0 || selectedVehicle?.name == item.name
            else if (selectedVehicle != null)
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
        Text(
            text = item.name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AvailableVehicles(
    modifier: Modifier,
    vehicles: List<Vehicle>,
    onSizeChanged: (IntSize) -> Unit,
) {
    val localConfiguration = LocalConfiguration.current
    if (localConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT)
        LazyHorizontalGrid(
            modifier = modifier,
            rows = GridCells.Fixed(2)
        ) {
            items(vehicles) {
                VehicleInfoItem(
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .wrapContentSize()
                        .onSizeChanged {
                            onSizeChanged(it)
                        },
                    item = it
                )
            }
        }
    else
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            vehicles.forEach {
                VehicleInfoItem(
                    modifier = Modifier
                        .weight(1f),
                    item = it
                )
            }

        }
}

@Composable
fun VehicleInfoItem(
    modifier: Modifier = Modifier,
    item: Vehicle
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Column {
            Text(
                text = "${item.name} (${item.totalNo} left)",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
//            Text(
//                text = "Available : ${item.totalNo}",
//                style = MaterialTheme.typography.labelSmall
//            )
            Text(
                text = "Max Distance: ${item.maxDistance}",
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = "Speed: ${item.speed}",
                style = MaterialTheme.typography.labelSmall
            )
            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}

