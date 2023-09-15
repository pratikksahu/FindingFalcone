package com.pratikk.findingfalcone.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pratikk.findingfalcone.ui.screens.viewmodel.MainViewModel

@Composable
fun Home(startFindFalcone:() -> Unit){
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(20.dp), contentAlignment = Alignment.Center){
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                modifier = Modifier.padding(bottom = 40.dp),
                text = "Hunt for Queen Al Falcone",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center)
            Text(
                text = "Join King Shan on the planet of Lengaburu as he embarks on a quest to find Queen Al Falcone. Will you help him choose the planets and vehicles wisely to succeed?",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(50.dp))
            Button(
                shape = MaterialTheme.shapes.small,
                onClick = {
                    startFindFalcone()
                }) {
                Icon(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    imageVector = Icons.Outlined.ArrowForward,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = "Start")
            }
            Text(text = "Begin the Search")
        }
    }
}
