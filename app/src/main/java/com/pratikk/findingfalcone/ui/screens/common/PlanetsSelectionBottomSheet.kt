package com.pratikk.findingfalcone.ui.screens.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pratikk.findingfalcone.data.planets.model.Planet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanetSelectionBottomSheet(
    list: () -> List<Planet>,
    selectedItem: Planet? = null,
    onSearch: (String) -> Unit = {},
    onClick: (Planet) -> Unit = {},
    hintText: String = ""
) {
    val focusManager = LocalFocusManager.current
    var searchParam by remember {
        mutableStateOf("")
    }
    val scope = rememberCoroutineScope()
    var sheetOpen by remember {
        mutableStateOf(false)
    }
    val sheetState = rememberModalBottomSheetState()


    CompositionLocalProvider(
        LocalTextInputService provides null
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    sheetOpen = !sheetOpen
                },
            value = if(selectedItem != null) "${selectedItem?.name} (Distance ${selectedItem?.distance})" else "",
            onValueChange = {},
            readOnly = true,
            label = {
                Text(text = hintText, maxLines = 1, overflow = TextOverflow.Ellipsis)
            },
            enabled = false,
            trailingIcon = {
                TextButton(onClick = { sheetOpen = true }) {
                    Text(text = if (selectedItem != null) "Change" else "Select")
                }
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
    LaunchedEffect(key1 = sheetOpen, block = {
        if(!sheetOpen){
            searchParam = ""
            onSearch("")
        }
    })
    if (sheetOpen)
        ModalBottomSheet(
            modifier = Modifier.navigationBarsPadding(),
            sheetState = sheetState,
            onDismissRequest = {
                sheetOpen = false
            }
        ) {
            Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                Text(
                    text = "Destination Choices",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = searchParam,
                    onValueChange = {
                        searchParam = it
                        onSearch(it)
                    },
                    label = {
                        Text(text = "Search", maxLines = 1, overflow = TextOverflow.Ellipsis)
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )
                LazyColumn {
                    items(list()) {
                        BaseDropdownItem(value = { "${it.name} (Distance ${it.distance})" },
                            onClick = {
                                focusManager.clearFocus()
                                onClick(it)
                                sheetOpen = false
                                scope.launch(Dispatchers.IO) {
                                    sheetState.hide()
                                }
                            })
                        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)
                    }
                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
}