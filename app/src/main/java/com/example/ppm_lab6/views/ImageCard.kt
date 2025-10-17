package com.example.ppm_lab6.views

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import com.example.ppm_lab6.models.PexelsPhoto
import com.example.ppm_lab6.R

@Composable
fun ImageCard(
    imageDetails: () -> Unit,
    gradient: Boolean,
    photo: PexelsPhoto,
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {

                imageDetails()

            },
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {

            AsyncImage(
                model = photo.src.medium ?: photo.src.large ?: photo.src.original,
                contentDescription = null,
                placeholder = painterResource(R.drawable.placeholder),
                error = painterResource(R.drawable.placeholder),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()

            )

            if (gradient) {

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black
                                ),
                                startY = 250f
                            )
                        )
                )
            }

        }
    }
}

@Composable
fun ImageCard(
    imageDetails: () -> Unit,
    gradient: Boolean,
    uri: String,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { imageDetails() },
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            AsyncImage(
                model = uri,
                contentDescription = null,
                placeholder = painterResource(R.drawable.placeholder),
                error = painterResource(R.drawable.placeholder),
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            if (gradient) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black),
                                startY = 250f
                            )
                        )
                )
            }
        }
    }
}
