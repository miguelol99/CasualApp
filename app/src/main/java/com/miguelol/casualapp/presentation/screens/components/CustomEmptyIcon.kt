package com.miguelol.casualapp.presentation.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.miguelol.casualapp.R
import com.miguelol.casualapp.presentation.theme.CasualAppTheme
import com.miguelol.casualapp.presentation.theme.PowderBlue

@Composable
fun CustomEmptyIcon(
    icon: Int,
    text: String
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CustomIcon(
            modifier = Modifier.size(50.dp),
            icon = icon,
            color = PowderBlue
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = text, color = PowderBlue)
    }
}

@Preview
@Composable
fun PreviewCustomEmptyIcon() {
    CasualAppTheme {
        CustomEmptyIcon(
            icon = R.drawable.round_person_add_alt_1_24,
            text = "You have no friends!"
        )
    }
}