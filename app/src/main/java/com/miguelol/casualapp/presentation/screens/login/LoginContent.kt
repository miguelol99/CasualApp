package com.miguelol.casualapp.presentation.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.miguelol.casualapp.R
import com.miguelol.casualapp.presentation.components.CustomTextField
import com.miguelol.casualapp.presentation.theme.CasualAppTheme

@Composable
fun LoginContent(
    modifier: Modifier = Modifier,
    uiState: LoginUiState,
    onEvent: (LoginEvents) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center
    ) {
        CustomTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.email,
            label = "Email",
            icon = R.drawable.round_person_24,
            onValueChange = { onEvent(LoginEvents.OnUsernameInput(it)) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.password,
            label = "Password",
            icon = R.drawable.round_password_24,
            keyBoardType = KeyboardType.Password,
            onValueChange = { onEvent(LoginEvents.OnPasswordInput(it)) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onEvent(LoginEvents.OnLogin)
            }
        ) {
            Text(text = "Login")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onEvent(LoginEvents.OnSignUp)
            }
        ) {
            Text(text = "Sign Up")
        }
        Row() {
            
        }
    }
}

@Preview
@Composable
fun PreviewLoginContent() {
    CasualAppTheme {
        LoginContent(
            uiState = LoginUiState(),
            onEvent = {}
        )
    }
}