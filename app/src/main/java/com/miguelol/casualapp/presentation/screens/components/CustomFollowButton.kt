package com.miguelol.casualapp.presentation.screens.components

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.miguelol.casualapp.domain.model.RequestState
import com.miguelol.casualapp.presentation.theme.CasualAppTheme

@Composable
fun CustomFollowButton(
    state: RequestState
) {

    val (text, color) = when (state) {
        RequestState.PENDING -> "Pending" to Color.Yellow
        RequestState.ACCEPTED -> "Followed" to Color.Green
        else -> "" to Color.Yellow
    }

    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = color
        ),
        onClick = { /*TODO*/ }
    ) {
        Text(text)
    }
}

@Preview
@Composable
fun PreviewCustomFollowButton() {
    CasualAppTheme {
        CustomFollowButton(
            state = RequestState.ACCEPTED
        )
    }
}