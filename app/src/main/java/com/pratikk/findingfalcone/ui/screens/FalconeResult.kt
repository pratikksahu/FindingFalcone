package com.pratikk.findingfalcone.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pratikk.findingfalcone.ui.screens.common.UIError
import com.pratikk.findingfalcone.ui.screens.common.UILoading
import com.pratikk.findingfalcone.ui.screens.common.UIState
import com.pratikk.findingfalcone.ui.screens.common.UISuccess
import com.pratikk.findingfalcone.ui.screens.viewmodel.FalconeResultViewModel

@Composable
fun FalconeResult(
    snackbarHostState: SnackbarHostState,
    falconeResultViewModel: FalconeResultViewModel,
    startAgain:() -> Unit
) {
    val uiState: UIState by falconeResultViewModel.uiState.collectAsState()
    val falconResponse by falconeResultViewModel.falconeResponse.collectAsState()
    LaunchedEffect(key1 = uiState, block = {
        if (uiState.isUIError)
            snackbarHostState.showSnackbar((uiState as UIError).error.toString())
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (falconResponse?.planetName != null){
                        Text(
                            modifier = Modifier.padding(bottom = 40.dp),
                            text = "Triumphant Victory!",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center)
                        Text(text = "Congratulations! You, along with King Shan, have successfully located Queen Al Falcone on ${falconResponse?.planetName} in ${falconeResultViewModel.totalTime.value} time. The kingdom is safe, and Al Falcone faces another 15 years of exile. Well done, brave adventurer!",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center)
                        Button(
                            modifier = Modifier.padding(vertical = 10.dp),
                            shape = MaterialTheme.shapes.extraSmall,
                            onClick = startAgain) {
                            Text(text = "Continue the Adventure")
                        }
                    }else{
                        Text(
                            modifier = Modifier.padding(bottom = 40.dp),
                            text = "Mission Incomplete",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center)
                        Text(text = "Oh no! Despite your valiant efforts and ${falconeResultViewModel.totalTime.value} time spent searching, Queen Al Falcone remains elusive. The kingdom faces uncertainty, but King Shan's determination remains unshaken. Will you try again and continue the quest?",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center)

                        Button(
                            modifier = Modifier.padding(vertical = 10.dp),
                            shape = MaterialTheme.shapes.extraSmall,
                            onClick = startAgain) {
                            Text(text = "Retry Mission")
                        }
                    }
                }
            }
        }
        if (uiState is UIError) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(20.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center) {
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
                        falconeResultViewModel.retry()
                    }) {
                        Text(text = "Try Again")
                    }
                }
            }
        }
    }
}