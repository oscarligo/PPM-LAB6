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

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    sealed interface Dest
    data object Images : Dest
    data object Profile : Dest
    data object Favorites : Dest
    data class Detail(val photo: com.example.ppm_lab6.models.PexelsPhoto) : Dest
    data class LocalDetail(val uri: String) : Dest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PPMLAB6Theme {
                val backStack = remember { mutableStateListOf<Dest>(Images) }

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
                                    openFavorites = { navigate(Favorites) }
                                )
                            }
                            is Detail -> NavEntry(key) {
                                DetailsScreen(
                                    onBack = { back() },
                                    photo = key.photo
                                )
                            }
                            is LocalDetail -> NavEntry(key) {
                                com.example.ppm_lab6.views.LocalDetailsScreen(
                                    onBack = { back() },
                                    uri = key.uri
                                )
                            }
                            is Profile -> NavEntry(key) { ProfileScreen(onBack = { back() }) }
                            is Favorites -> NavEntry(key) {
                                FavoritesScreen(
                                    onBack = { back() },
                                    openDetail = { photo -> navigate(Detail(photo)) },
                                    openLocalDetail = { uri -> navigate(LocalDetail(uri)) }
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}