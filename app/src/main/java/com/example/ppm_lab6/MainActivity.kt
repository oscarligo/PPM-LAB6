package com.example.ppm_lab6

import ImagesScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.ppm_lab6.ui.theme.PPMLAB6Theme
import com.example.ppm_lab6.views.ImagesScreen
import kotlin.collections.removeLast
import kotlin.compareTo


data object Photos
data class Screen(val id: String)



@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    sealed interface Dest
    data object Images : Dest
    data object Settings : Dest
    data class Detail(val id: String) : Dest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PPMLAB6Theme {

                val backStack = remember { mutableListOf<Dest>(Images) }

                // Helpers
                fun navigate(dest: Dest) { backStack.add(dest) }
                fun back() {
                    if (backStack.size > 1) backStack.removeLast()
                    else finish() // opción: cerrar app si en root
                }

                NavDisplay(
                    backStack = backStack,
                    onBack = { back() },
                    entryProvider = { key ->
                        when (key) {
                            is Images -> NavEntry(key) {
                                ImagesScreen(
                                    profle = { id -> navigate(Detail(id)) },
                                    imageDetails = { navigate(Settings) }
                                )
                            }
                            is Detail -> NavEntry(key) {
                                DetailScreen(
                                    id = key.id,
                                    onBack = { back() }
                                )
                            }
                            is Settings -> NavEntry(key) {
                                SettingsScreen(onBack = { back() })
                            }
                        }
                    }
                )

            }
        }
    }
}


