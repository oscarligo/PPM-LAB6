package com.example.ppm_lab6.views

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Box


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onBack : () -> Unit) {

    Scaffold(
        topBar = {

            Box {
                TopAppBar(
                    title = { Text("Perfil", textAlign = TextAlign.Center) },
                    colors = topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.height(30.dp)
                )


            }
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.height(40.dp)
            ) {

            }
        },

        ) { paddingValues ->
        Surface(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {



        }
    }
}

@Preview
@Composable
fun PreviewSettingsScreen() {

}