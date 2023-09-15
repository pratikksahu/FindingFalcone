package com.pratikk.findingfalcone.NavGraph

import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pratikk.findingfalcone.ui.screens.FalconeResult
import com.pratikk.findingfalcone.ui.screens.FindFalcone
import com.pratikk.findingfalcone.ui.screens.viewmodel.MainViewModel
import com.pratikk.findingfalcone.ui.screens.Home
import com.pratikk.findingfalcone.ui.screens.viewmodel.FalconeResultViewModel
import com.pratikk.findingfalcone.ui.screens.viewmodel.FalconeViewModel

object Routes {
    const val HOME = "HOME"
    const val FIND_FALCONE = "FIND_FALCONE"
    const val FIND_FALCONE_RESULT = "FIND_FALCONE_RESULT"
}

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = Routes.HOME,
        enterTransition = {
            slideInHorizontally(
                tween(500), initialOffsetX = { it })
        },
        exitTransition = {
            scaleOut(tween(200), targetScale = 0.9f) +
                    slideOutHorizontally(
                        tween(500),
                        targetOffsetX = { -it })
        },
        popEnterTransition = {
            slideInHorizontally(
                tween(500), initialOffsetX = { -it })
        },
        popExitTransition = {
            scaleOut(tween(200), targetScale = 0.9f) +
                    slideOutHorizontally(
                        tween(500),
                        targetOffsetX = { it })
        }
    ) {
        composable(route = Routes.HOME) {
            Home(startFindFalcone = {
                navController.navigate(Routes.FIND_FALCONE)
            })
        }
        composable(route = Routes.FIND_FALCONE) { backStackEntry ->
            val falconeViewModel = viewModel<FalconeViewModel>(backStackEntry)
            val falconeResultViewModel = viewModel<FalconeResultViewModel>(backStackEntry)
            FindFalcone(
                mainViewModel = mainViewModel,
                falconeViewModel = falconeViewModel,
                findFalcone = {
                    falconeResultViewModel.getFaclonResult(falconeViewModel.selectedPlanetMap.map { it.value },falconeViewModel.selectedVehiclesMap.map { it.value })
                    navController.navigate(Routes.FIND_FALCONE_RESULT)
                    falconeResultViewModel.setTotalTime(falconeViewModel.totalTime.value)
                })
        }
        composable(route = Routes.FIND_FALCONE_RESULT) {backStackEntry ->
            val findFalconeEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Routes.FIND_FALCONE)
            }
            val falconeResultViewModel = viewModel<FalconeResultViewModel>(findFalconeEntry)
            FalconeResult(mainViewModel = mainViewModel, falconeResultViewModel = falconeResultViewModel, startAgain = {
                navController.navigate(Routes.FIND_FALCONE){
                    popUpTo(Routes.FIND_FALCONE){
                        inclusive = true
                    }
                }
            })
        }
    }
}