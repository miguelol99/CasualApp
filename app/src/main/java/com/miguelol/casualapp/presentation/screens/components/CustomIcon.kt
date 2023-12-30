package com.miguelol.casualapp.presentation.screens.components

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.miguelol.casualapp.R
import com.miguelol.casualapp.presentation.theme.CasualAppTheme

@Composable
fun CustomIcon(
    modifier: Modifier = Modifier,
    icon: Int,
    color: Color = Color.White,
    contentDescription: String? = null
) {
    Icon(
        modifier = modifier,
        painter = painterResource(id = icon),
        contentDescription = contentDescription,
        tint = color
    )
}

@Preview
@Composable
fun PreviewCustomIcon() {
    CasualAppTheme {
        CustomIcon(
            icon = R.drawable.round_check_24
        )
    }
}