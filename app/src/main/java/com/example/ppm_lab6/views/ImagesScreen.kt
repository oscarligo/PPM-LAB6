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
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import com.example.ppm_lab6.images
import com.example.ppm_lab6.models.ImageClass


@Composable
fun ImagesList(images: List<ImageClass>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp),
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        modifier = Modifier.padding(3.dp)
    ){
        items(images) { image ->
            ImageCard(image)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagesScreen(profileScreen: (String) -> Unit, imageDetails: () -> Unit) {

    Scaffold(
        topBar = {

            Box {
                TopAppBar(
                    title = { Text("", textAlign = TextAlign.Center) },
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
            ImagesList(images = images)
        }
    }
}

@Preview
@Composable
fun PreviewImagesScreen() {

    ImagesScreen({},{})


}