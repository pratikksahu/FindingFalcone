package com.pratikk.findingfalcone.ui.screens.common

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.view.ViewTreeObserver
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pratikk.findingfalcone.data.planets.model.Planet
import com.pratikk.findingfalcone.ui.theme.FindingFalconeTheme


@Composable
fun BaseExposedDropdown(
    modifier: Modifier = Modifier,
    list: () -> List<Planet>,
    selectedItem: Planet? = null,
    onSearch: (String) -> Unit = {},
    onClick: (Planet) -> Unit = {},
    hintText: String = ""
) {
    val keyboardVisible by keyboardAsState()
    val focusManager = LocalFocusManager.current
    var expanded by remember { mutableStateOf(false) }
    val shouldRemoveFocus by remember {
        derivedStateOf {
            !expanded && !keyboardVisible
        }
    }
    LaunchedEffect(key1 = shouldRemoveFocus, block = {
        if (shouldRemoveFocus)
            focusManager.clearFocus()
    })
    var searchParam by remember {
        mutableStateOf("")
    }
    val density = LocalDensity.current
    var itemWidth by remember {
        mutableStateOf(0)
    }
    val maxWidth by remember(itemWidth) {
        derivedStateOf{
            if(itemWidth == 0)
                return@derivedStateOf 200.dp
            with(density) { itemWidth.toDp() }
        }
    }
    LaunchedEffect(key1 = expanded, block = {
        if(!expanded)
            onSearch("")
    })
    Column(
        modifier = modifier.clickable {
            expanded = !expanded
        }) {
        CompositionLocalProvider(
            LocalTextInputService provides null
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        expanded = !expanded
                    }
                    .onSizeChanged {
                        itemWidth = it.width
                    },
                value = selectedItem?.name ?: "",
                onValueChange = {},
                readOnly = true,
                label = {
                    Text(text = hintText, maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                enabled = false,
                trailingIcon = {
                    DropdownTrailingIcon(expanded = expanded)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledContainerColor = Color.Transparent,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledSupportingTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledPrefixColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledSuffixColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        DropdownMenu(
            modifier = Modifier.requiredSizeIn(minWidth = maxWidth),
            expanded = expanded, onDismissRequest = { expanded = false }) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                value = searchParam,
                onValueChange = {
                    searchParam = it
                    onSearch(it)
                },
                label = {
                    Text(text = "Search", maxLines = 1, overflow = TextOverflow.Ellipsis)
                }, keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )
            list().forEach {
                BaseDropdownItem(value = { "${it.name} (Distance ${it.distance})" },
                    onClick = {
                        focusManager.clearFocus()
                        onClick(it)
                        expanded = false
                    })
            }
        }

    }
}

@Composable
fun DropdownTrailingIcon(expanded: Boolean) {

    val angle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "DropdownAnimation"
    )
    Icon(
        Icons.Rounded.KeyboardArrowDown,
        null,
        Modifier.graphicsLayer {
            rotationX = angle
        }
    )
    Modifier.rotate(if (expanded) 180f else 0f)
}

@Composable
fun BaseDropdownItem(value: () -> String, onClick: () -> Unit) {
    DropdownMenuItem(text = {
        Text(
            text = value(),
            textAlign = TextAlign.Start
        )
    }, onClick = onClick)
}

@Preview(uiMode = UI_MODE_NIGHT_NO)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun CommonDropdownPreview() {
    FindingFalconeTheme {
        BaseExposedDropdown(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.background)
                .fillMaxWidth(),
            list = { listOf() },
            hintText = "Hello",
            onClick = {}
        )
    }
}

@Composable
fun keyboardAsState(): State<Boolean> {
    val view = LocalView.current
    var isImeVisible by remember { mutableStateOf(false) }

    DisposableEffect(LocalWindowInfo.current) {
        val listener = ViewTreeObserver.OnPreDrawListener {
            isImeVisible = ViewCompat.getRootWindowInsets(view)
                ?.isVisible(WindowInsetsCompat.Type.ime()) == true
            true
        }
        view.viewTreeObserver.addOnPreDrawListener(listener)
        onDispose {
            view.viewTreeObserver.removeOnPreDrawListener(listener)
        }
    }
    return rememberUpdatedState(isImeVisible)
}