package com.miguelol.casualapp.presentation.screens.components

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.miguelol.casualapp.R
import com.miguelol.casualapp.presentation.theme.CasualAppTheme

@Composable
fun CustomText(
    text: String,
) {
    Text(text = text)
}

@Preview
@Composable
fun PreviewCustomText() {
    CasualAppTheme {
        CustomText(text = "Texto de prueba")
    }
}