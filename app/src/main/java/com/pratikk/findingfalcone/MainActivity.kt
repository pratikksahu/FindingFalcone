package com.pratikk.findingfalcone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pratikk.findingfalcone.NavGraph.AppNavGraph
import com.pratikk.findingfalcone.NavGraph.Routes
import com.pratikk.findingfalcone.ui.theme.FindingFalconeTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FindingFalconeTheme {
                val snackbarHostState = remember {
                    SnackbarHostState()
                }
                val navController = rememberNavController()
                val currentDestination by navController.currentBackStackEntryAsState()
                Scaffold(snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState)
                },
                    topBar = {
                        AnimatedVisibility(
                            visible = currentDestination?.destination?.route != Routes.HOME,
                            enter = expandVertically(
                                expandFrom = Alignment.Top,
                                initialHeight = { 0 }) + fadeIn(),
                            exit = shrinkVertically(
                                shrinkTowards = Alignment.Top,
                                targetHeight = { 0 }) + fadeOut()
                        ) {
                            val title by remember(currentDestination) {
                                derivedStateOf {
                                    when(currentDestination?.destination?.route){
                                        Routes.FIND_FALCONE,Routes.FIND_FALCONE_RESULT -> "Finding Falcon!"
                                        else -> ""
                                    }
                                }
                            }
                            TopAppBar(
                                title = {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = FontFamily.SansSerif
                                    )
                                },
                                colors = TopAppBarDefaults.smallTopAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                navigationIcon = {
                                    IconButton(onClick = { navController.popBackStack() }) {
                                        Icon(
                                            imageVector = Icons.Outlined.ArrowBack,
                                            contentDescription = "Back"
                                        )
                                    }
                                }
                            )
                        }
                    }) {
                    AppNavGraph(
                        modifier = Modifier
                            .padding(it)
                            .background(color = MaterialTheme.colorScheme.background),
                        navController = navController,
                        snackbarHostState = snackbarHostState
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FindingFalconeTheme {
        Greeting("Android")
    }
}