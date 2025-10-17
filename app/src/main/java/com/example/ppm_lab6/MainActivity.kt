package com.example.ppm_lab6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.ppm_lab6.ui.theme.PPMLAB6Theme
import com.example.ppm_lab6.views.DetailsScreen
import com.example.ppm_lab6.views.ImagesScreen
import com.example.ppm_lab6.views.ProfileScreen
import com.example.ppm_lab6.views.FavoritesScreen
import com.example.ppm_lab6.models.PexelsPhoto

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    sealed interface Dest
    data object Images : Dest
    data object Profile : Dest
    data object Favorites : Dest
    data class Detail(val photo: PexelsPhoto) : Dest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PPMLAB6Theme {
                val backStack = remember { mutableStateListOf<Dest>(Images) }

                // Simple favorites state (no repository)
                val favoriteRemotes = remember { mutableStateListOf<PexelsPhoto>() }
                val favoriteLocals = remember { mutableStateListOf<String>() }

                fun isFavorite(photo: PexelsPhoto) = favoriteRemotes.any { it.id == photo.id }
                fun toggleFavorite(photo: PexelsPhoto) {
                    val idx = favoriteRemotes.indexOfFirst { it.id == photo.id }
                    if (idx >= 0) favoriteRemotes.removeAt(idx) else favoriteRemotes.add(photo)
                }
                fun addLocalFavorite(uri: String) {
                    if (favoriteLocals.contains(uri)) return
                    favoriteLocals.add(uri)
                }

                fun navigate(dest: Dest) { backStack.add(dest) }
                fun back() { if (backStack.size > 1) backStack.removeLast() else finish() }

                NavDisplay(
                    backStack = backStack,
                    onBack = { back() },
                    entryProvider = { key ->
                        when (key) {
                            is Images -> NavEntry(key) {
                                ImagesScreen(
                                    openDetail = { photo -> navigate(Detail(photo)) },
                                    openProfile = { navigate(Profile) },
                                    openFavorites = { navigate(Favorites) },
                                    onAddLocalFavorite = { uri -> addLocalFavorite(uri) }
                                )
                            }
                            is Detail -> NavEntry(key) {
                                DetailsScreen(
                                    onBack = { back() },
                                    photo = key.photo,
                                    isFavorite = { p -> isFavorite(p) },
                                    toggleFavorite = { p -> toggleFavorite(p) }
                                )
                            }
                            is Profile -> NavEntry(key) { ProfileScreen(onBack = { back() }) }
                            is Favorites -> NavEntry(key) {
                                FavoritesScreen(
                                    onBack = { back() },
                                    remoteFavorites = favoriteRemotes,
                                    localFavorites = favoriteLocals
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}