package com.example.ppm_lab6.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.ui.platform.LocalContext
import com.example.ppm_lab6.data.DatabaseProvider
import com.example.ppm_lab6.data.FavoriteLocalEntity
import com.example.ppm_lab6.data.FavoriteRemoteEntity
import com.example.ppm_lab6.data.JsonProvider
import com.example.ppm_lab6.models.PexelsPhoto
import com.squareup.moshi.adapter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(onBack: () -> Unit, openDetail: (PexelsPhoto) -> Unit, openLocalDetail: (String) -> Unit) {
    val context = LocalContext.current
    val db = remember { DatabaseProvider.get(context) }
    val remotes by db.favoriteRemoteDao().observeAll().collectAsState(initial = emptyList())
    val locals by db.favoriteLocalDao().observeAll().collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            Box {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent)
                            )
                        )
                )
                TopAppBar(
                    title = { Text("Favorites", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White
                    ),
                    modifier = Modifier.height(70.dp)
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
        }
    ) { padding ->
        Surface(
            modifier = Modifier.fillMaxSize().padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
            val moshi = JsonProvider.moshi
            val adapter = remember { moshi.adapter(PexelsPhoto::class.java) }
            val isEmpty = remotes.isEmpty() && locals.isEmpty()
            if (isEmpty) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay favoritos aún", color = Color.White)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 150.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxSize().padding(6.dp)
                ) {
                    items(remotes, key = { it.id }) { entity: FavoriteRemoteEntity ->
                        val photo = runCatching { adapter.fromJson(entity.json) }.getOrNull()
                        if (photo != null) {
                            ImageCard(
                                imageDetails = { openDetail(photo) },
                                gradient = true,
                                photo = photo
                            )
                        }
                    }
                    items(locals, key = { it.uri }) { entity: FavoriteLocalEntity ->
                        ImageCard(
                            imageDetails = { openLocalDetail(entity.uri) },
                            gradient = true,
                            uri = entity.uri
                        )
                    }
                }
            }
        }
    }
}
