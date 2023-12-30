package com.miguelol.casualapp.presentation.screens.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.miguelol.casualapp.presentation.theme.CasualAppTheme

@Composable
fun CustomAsyncImage(
    image: String,
    progressIndicatorColor: Color = MaterialTheme.colorScheme.background,
    contentDescription: String? = null,
    modifier: Modifier
) {

    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        CircularProgressIndicator(color = progressIndicatorColor)
        AsyncImage(
            modifier = modifier,
            model = image,
            contentScale = ContentScale.Crop,
            contentDescription = contentDescription
        )
    }
}

@Preview
@Composable
fun PreviewCustomAsyncImage(){
    CasualAppTheme() {
        CustomAsyncImage(
            image = "",
            modifier = Modifier)
    }
}