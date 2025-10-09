// kotlin
package com.example.ppm_lab6.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ppm_lab6.models.PexelsPhoto
import com.example.ppm_lab6.models.PexelsResponse
import com.example.ppm_lab6.models.PexelsService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagesScreen(
    openDetail: (PexelsPhoto) -> Unit,
    openProfile: () -> Unit = {},
    initialPage: Int = 1,
    perPage: Int = 14,
) {
    var loading by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var page by rememberSaveable { mutableStateOf(initialPage) }

    var photos by remember { mutableStateOf<List<PexelsPhoto>>(emptyList()) }
    var currentCall by remember { mutableStateOf<Call<PexelsResponse>?>(null) }
    val gridState = rememberLazyGridState()


    fun fetch(newPage: Int = page) {
        currentCall?.cancel()
        loading = true
        error = null

        val call = PexelsService.api.getCurated(page = newPage, perPage = perPage)
        currentCall = call

        call.enqueue(object : Callback<PexelsResponse> {
            override fun onResponse(call: Call<PexelsResponse>, response: Response<PexelsResponse>) {
                loading = false
                if (response.isSuccessful) {
                    val list = response.body()?.photos.orEmpty()
                    photos = list
                    page = newPage
                } else {
                    error = "HTTP ${response.code()}"
                }
            }
            override fun onFailure(call: Call<PexelsResponse>, t: Throwable) {
                if (call.isCanceled) return
                loading = false
                error = t.message ?: "Network error"
            }
        })
    }

    LaunchedEffect(photos, gridState.firstVisibleItemIndex) {
        val lastVisible = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        if (!loading && error == null && lastVisible >= photos.size - 3) {
            // Load next page when 3 items from the end
            fetch(page + 1)
        }
    }



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
            ) { /* Add buttons if needed */ }
        },
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            when {
                photos.isNotEmpty() -> {

                    LazyVerticalGrid(
                        state = gridState,
                        columns = GridCells.Adaptive(minSize = 150.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp)
                    ) {
                        items(photos, key = { it.id }) { photo ->
                            ImageCard(
                                imageDetails = { openDetail(photo) },
                                gradient = true,
                                photo = photo,
                            )
                        }
                    }
                }
                loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Loading...")
                    }
                }
                error != null -> {
                    Text("Error: $error", modifier = Modifier.padding(16.dp))
                }
                else -> {
                    Text("No photos available", modifier = Modifier.padding(16.dp))
                }
            }

            if (photos.isEmpty() && !loading && error == null) {
                fetch(page)
            }
        }
    }
}