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
import com.example.ppm_lab6.models.PexelsPhoto

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    sealed interface Dest
    data object Images : Dest
    data object Profile : Dest
    data class Detail(val photo: PexelsPhoto) : Dest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PPMLAB6Theme {
                // Use a SnapshotStateList so changes trigger recomposition
                val backStack = remember { mutableStateListOf<Dest>(Images) }

                fun navigate(dest: Dest) { backStack.add(dest) }
                fun back() {
                    if (backStack.size > 1) backStack.removeLast() else finish()
                }

                NavDisplay(
                    backStack = backStack,
                    onBack = { back() },
                    entryProvider = { key ->
                        when (key) {
                            is Images -> NavEntry(key) {
                                ImagesScreen(
                                    openDetail = { photo -> navigate(Detail(photo)) },
                                    openProfile = { navigate(Profile) }
                                )
                            }
                            is Detail -> NavEntry(key) {
                                DetailsScreen(
                                    onBack = { back() },
                                    photo = key.photo
                                )

                            }
                            is Profile -> NavEntry(key) {
                                ProfileScreen(onBack = { back() })
                            }

                            else -> error("Unknown key $key")
                        }
                    }
                )
            }
        }
    }
}