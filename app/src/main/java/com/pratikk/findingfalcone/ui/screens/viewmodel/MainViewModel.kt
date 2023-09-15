package com.pratikk.findingfalcone.ui.screens.viewmodel

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class MainViewModel:ViewModel() {
    val snackBarHost by mutableStateOf(SnackbarHostState())
}