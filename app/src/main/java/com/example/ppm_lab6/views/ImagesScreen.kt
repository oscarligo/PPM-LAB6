// kotlin
package com.example.ppm_lab6.views

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ppm_lab6.models.PexelsPhoto
import com.example.ppm_lab6.models.PexelsResponse
import com.example.ppm_lab6.models.PexelsService
import com.example.ppm_lab6.data.FavoriteLocalEntity
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.compose.material3.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.ui.draw.clip
import androidx.compose.material3.IconButton
import androidx.compose.ui.zIndex
import kotlinx.coroutines.FlowPreview
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Add
import com.example.ppm_lab6.data.DatabaseProvider
import com.example.ppm_lab6.data.RecentSearch
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.PaddingValues
import android.content.Intent


@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun ImagesScreen(
    openDetail: (PexelsPhoto) -> Unit,
    openProfile: () -> Unit = {},
    openFavorites: () -> Unit = {},
    initialPage: Int = 1,
    perPage: Int = 14
) {
    var loading by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var page by rememberSaveable { mutableStateOf(initialPage) }
    var endReached by rememberSaveable { mutableStateOf(false) }

    var photos by remember { mutableStateOf<List<PexelsPhoto>>(emptyList()) }
    var currentCall by remember { mutableStateOf<Call<PexelsResponse>?>(null) }
    val gridState = rememberLazyGridState() // state for LazyVerticalGrid
    var query by remember { mutableStateOf("") } // search bar state

    val context = LocalContext.current
    val db = remember { DatabaseProvider.get(context) }
    val dao = remember { db.recentSearchDao() }
    val favoriteLocalDao = remember { db.favoriteLocalDao() }
    val scope = rememberCoroutineScope()
    val recent by dao.observeRecent(limit = 10).collectAsState(initial = emptyList())

    // Document picker launcher with persistable permission
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: SecurityException) {
                // ignore if not persistable; still try to save
            }
            scope.launch { favoriteLocalDao.upsert(FavoriteLocalEntity(uri = uri.toString(), updatedAt = System.currentTimeMillis())) }
        }
    }

    DisposableEffect(Unit) {
        onDispose { currentCall?.cancel() }
    }

    fun fetch(newPage: Int = page, reset: Boolean = false) {
        if (loading) return
        if (reset) {
            endReached = false
            photos = emptyList()
        } else if (endReached) {
            return
        }
        currentCall?.cancel()
        loading = true
        error = null

        val call = if (query.isBlank()) {
            PexelsService.api.getCurated(page = newPage, perPage = perPage)
        } else {
            PexelsService.api.search(query = query.trim(), page = newPage, perPage = perPage)
        }
        currentCall = call

        call.enqueue(object : Callback<PexelsResponse> {
            override fun onResponse(call: Call<PexelsResponse>, response: Response<PexelsResponse>) {
                loading = false
                if (response.isSuccessful) {
                    val body = response.body()
                    val list = body?.photos.orEmpty()
                    endReached = body?.nextPage == null || list.isEmpty()

                    photos = if (reset || newPage <= initialPage) list else photos + list
                    page = newPage

                    // Save successful query as recent (non-blank)
                    val q = query.trim()
                    if (q.isNotEmpty()) {
                        scope.launch {
                            dao.upsert(RecentSearch(query = q, updatedAt = System.currentTimeMillis()))
                            dao.trim(limit = 10)
                        }
                    }
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

    // Initial load
    LaunchedEffect(Unit) {
        if (photos.isEmpty()) fetch(initialPage, reset = true)
    }

    // Trigger next page when scrolled near the end
    LaunchedEffect(gridState, photos, endReached) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0 }
            .map { lastIndex -> lastIndex >= photos.size - 4 }
            .distinctUntilChanged()
            .filter { it && !loading && error == null && !endReached && photos.isNotEmpty() }
            .collect { fetch(page + 1) }
    }

    // Debounced query changes -> reset and fetch
    LaunchedEffect(Unit) {
        snapshotFlow { query }
            .debounce(500)
            .distinctUntilChanged()
            .drop(1) // skip initial empty query; initial load already handled
            .collect {
                fetch(initialPage, reset = true)
            }
    }

    Scaffold(
        topBar = {
            Box {
                // Gradient background for top bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
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
                    modifier = Modifier.height(30.dp)
                )
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                            )
                        )
                )
                BottomAppBar(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.matchParentSize()
                        .height(10.dp)
                ) { }
            }
        },


    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    photos.isNotEmpty() -> {
                        LazyVerticalGrid(
                            state = gridState,
                            columns = GridCells.Adaptive(minSize = 150.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            contentPadding = PaddingValues(
                                top = 6.dp,
                                start = 6.dp,
                                end = 6.dp,
                                bottom = 96.dp
                            ),
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            items(photos, key = { it.id }) { photo ->
                                ImageCard(
                                    imageDetails = { openDetail(photo) },
                                    gradient = true,
                                    photo = photo,
                                )
                            }
                            if (loading) {
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }
                        }
                    }
                    loading -> {
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(top = 64.dp)
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    error != null -> {
                        // Compact error message, not full-screen
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp)
                        ) {
                            Text("Error: $error")
                        }
                    }
                    else -> {
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp)
                        ) {
                            val msg = if (query.isNotBlank()) "No results for \"$query\"" else "No photos available"
                            Text(msg)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                        .align(Alignment.TopCenter)
                        .zIndex(1f)
                ) {
                    Column {
                        SearchBar(
                            query = query,
                            onQueryChange = { query = it },
                            onClear = { query = "" }
                        )
                        if (recent.isNotEmpty()) {
                            Spacer(Modifier.height(8.dp))
                            RecentSearchRow(
                                items = recent,
                                onSelect = { selected -> query = selected }
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                        .align(Alignment.BottomCenter)
                        .zIndex(1f)
                ) {
                    BottomActions(
                        onProfile = { openProfile() },
                        onFavorites = { openFavorites() },
                        onPickFromGallery = { galleryLauncher.launch(arrayOf("image/*")) }
                    )
                }


            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.7f))
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search Filter", color = Color.White) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color.White
                )
            },
            trailingIcon = if (query.isNotEmpty()) {
                {
                    IconButton(onClick = onClear) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = Color.White
                        )
                    }
                }

            }
            else null,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp)
                .background(Color.Transparent),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                cursorColor = Color.White
            ),
            textStyle = LocalTextStyle.current.copy(color = Color.White)
        )
    }
}

@Composable
fun RecentSearchRow(
    items: List<RecentSearch>,
    onSelect: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(items) { item ->
                Button(
                    onClick = { onSelect(item.query) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black.copy(alpha = 0.7f),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = item.query,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun BottomActions(
    onProfile: () -> Unit,
    onFavorites: () -> Unit,
    onPickFromGallery: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.7f))
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = onProfile,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White)
        ) {
            Icon(Icons.Default.Person, contentDescription = null)
            Text( "Profile", modifier = Modifier.padding(start = 4.dp) )

        }

        Button(
            onClick = onFavorites,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White)
        ) {
            Icon(Icons.Default.Favorite, contentDescription = null)
            Text( "Favorites", modifier = Modifier.padding(start = 4.dp) )

        }

        Button(
            onClick = onPickFromGallery,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Text( "Add", modifier = Modifier.padding(start = 4.dp) )
        }
    }
}
