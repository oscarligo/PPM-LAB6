package com.example.ppm_lab6.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.material3.Button
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.BottomAppBar
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.example.ppm_lab6.data.DatabaseProvider
import com.example.ppm_lab6.data.FavoriteRemoteDao
import com.example.ppm_lab6.data.FavoriteRemoteEntity
import com.example.ppm_lab6.data.JsonProvider
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    onBack: () -> Unit,
    photo: com.example.ppm_lab6.models.PexelsPhoto,
) {
    val context = LocalContext.current
    val db = remember { DatabaseProvider.get(context) }
    val dao: FavoriteRemoteDao = remember { db.favoriteRemoteDao() }
    val scope = rememberCoroutineScope()

    val isFavCount by dao.isFavoriteFlow(photo.id).collectAsState(initial = 0)
    val isFav = isFavCount > 0

    Scaffold(
        topBar = {
            Box {
                // Gradient background behind top bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent)
                            )
                        )
                )
                TopAppBar(
                    title = { Text("", textAlign = TextAlign.Center, color = Color.White) },
                    colors = topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White
                    ),
                    modifier = Modifier.height(50.dp),
                    navigationIcon = {
                        IconButton(onClick = { onBack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    }

                )
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            ) {
                // Gradient behind bottom bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                            )
                        )
                )
                BottomAppBar(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.height(40.dp)
                ) {}
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
                    .height(450.dp), // Adjust size as needed
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
                    ImageCard(imageDetails = { onBack() }, gradient = false, photo = photo)
                    // author and title
                    Text(text = photo.alt ?: "No Title", style = MaterialTheme.typography.titleMedium)
                    Text(text = "Author: " + photo.photographer, style = MaterialTheme.typography.bodyMedium)

                    if (isFav) {
                        Text(text = "Saved as Favorite", color = MaterialTheme.colorScheme.primary)
                    }

                    Button(onClick = {
                        scope.launch {
                            if (isFav) {
                                dao.delete(photo.id)
                            } else {
                                val json = JsonProvider.moshi.adapter(com.example.ppm_lab6.models.PexelsPhoto::class.java).toJson(photo)
                                dao.upsert(FavoriteRemoteEntity(id = photo.id, json = json, updatedAt = System.currentTimeMillis()))
                            }
                        }
                    }) {
                        Text(text = if (isFav) "Remove from Favorites" else "Save as Favorite")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewDetailsScreen() {

}
