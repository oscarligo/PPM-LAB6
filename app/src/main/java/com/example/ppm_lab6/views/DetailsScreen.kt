package com.example.ppm_lab6.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ppm_lab6.models.PexelsPhoto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    onBack: () -> Unit,
    photo: PexelsPhoto,

    ) {

    Scaffold(
        topBar = {
            Box {
                TopAppBar(
                    title = { Text("", textAlign = TextAlign.Center) },
                    colors = topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.height(50.dp),
                    navigationIcon = {
                        IconButton(onClick = { onBack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Localized description"
                            )
                        }
                    }

                )
            }
        },

        ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .width(320.dp)
                    .height(400.dp), // Adjust size as needed
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ImageCard(imageDetails = {onBack()}, gradient = false, photo = photo)
                    Text(text = photo.photographer, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewDetailsScreen() {

}

