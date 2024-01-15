package com.miguelol.casualapp.presentation.screens.components

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.miguelol.casualapp.R
import com.miguelol.casualapp.presentation.screens.createplan.CreatePlanEvents
import com.miguelol.casualapp.presentation.theme.CasualAppTheme
import com.miguelol.casualapp.presentation.theme.PowderBlue

@Composable
fun CustomFloatingActionButton(
    onCLick: () -> Unit,
    icon: Int,
    contentDescription: String? = null,
) {

        FloatingActionButton(onClick = { onCLick() } ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = contentDescription,
            )
        }
}

@Preview
@Composable
fun PreviewCustomFloatingActionButton() {
    CasualAppTheme {
        CustomFloatingActionButton(
            onCLick = { },
            icon = R.drawable.round_check_24
        )
    }
}