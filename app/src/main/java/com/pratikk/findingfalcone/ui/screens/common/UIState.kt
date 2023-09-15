package com.pratikk.findingfalcone.ui.screens.common

sealed interface UIState{
    val isUIError
        get() = this is UIError
}

object UILoading:UIState
object UISuccess:UIState
class UIError(val error:String?):UIState