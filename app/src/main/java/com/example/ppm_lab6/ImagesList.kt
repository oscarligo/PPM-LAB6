package com.example.ppm_lab6

import androidx.compose.runtime.mutableStateListOf
import com.example.ppm_lab6.models.ImageClass

val images = mutableStateListOf(
    ImageClass( "Montaña", "Hermosa vista de montaña", "https://picsum.photos/200/200?random=1"),
    ImageClass( "Océano", "Vista del océano azul", "https://picsum.photos/200/200?random=2"),
    ImageClass("Bosque", "Paisaje de bosque verde", "https://picsum.photos/200/200?random=3"),
    ImageClass("Desierto", "Arena y dunas en el desierto", "https://picsum.photos/200/200?random=4")
)
