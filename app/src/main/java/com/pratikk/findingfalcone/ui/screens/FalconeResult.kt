package com.pratikk.findingfalcone.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pratikk.findingfalcone.ui.screens.common.UIError
import com.pratikk.findingfalcone.ui.screens.common.UILoading
import com.pratikk.findingfalcone.ui.screens.common.UIState
import com.pratikk.findingfalcone.ui.screens.common.UISuccess
import com.pratikk.findingfalcone.ui.screens.viewmodel.FalconeResultViewModel
import com.pratikk.findingfalcone.ui.screens.viewmodel.MainViewModel

@Composable
fun FalconeResult(
    mainViewModel: MainViewModel,
    falconeResultViewModel: FalconeResultViewModel,
    startAgain:() -> Unit
) {
    val uiState: UIState by falconeResultViewModel.uiState.collectAsState()
    val falconResponse by falconeResultViewModel.falconeResponse.collectAsState()
    LaunchedEffect(key1 = uiState, block = {
        if (uiState.isUIError)
            mainViewModel.snackBarHost.showSnackbar((uiState as UIError).error.toString())
    })
    var composableLoaded by remember {
        mutableStateOf(false)
    }
    SideEffect {
        composableLoaded = true
    }
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
        if (uiState is UISuccess) {
            if (falconResponse?.planetName != null)
                Box(
                    modifier = Modifier.fillMaxSize().padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Congratulations",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "On",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Finding Falcone",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(text = "King Shan is mighty pleased",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(50.dp))
                        Text(text = "Time Taken: ${falconeResultViewModel.totalTime.value}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium)
                        Text(text = "Planet Found: ${falconResponse?.planetName}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium)
                        Button(
                            modifier = Modifier.padding(vertical = 10.dp),
                            shape = MaterialTheme.shapes.extraSmall,
                            onClick = startAgain) {
                            Text(text = "Start Again")
                        }
                    }
                }
            else {
                Box(
                    modifier = Modifier.fillMaxSize().padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Unfortunately",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium)
                        Text(text = "Falcone remains at large",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium)
                        Text(text = "King Shan is far from pleased",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium)
                        Button(
                            modifier = Modifier.padding(vertical = 10.dp),
                            shape = MaterialTheme.shapes.extraSmall,
                            onClick = startAgain) {
                            Text(text = "Start Again")
                        }
                    }
                }
            }
        }
    }
}