package com.miguelol.casualapp.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.miguelol.casualapp.R
import com.miguelol.casualapp.presentation.screens.components.CustomIcon
import com.miguelol.casualapp.presentation.theme.CasualAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    modifier: Modifier,
    value: String,
    label: String,
    icon: Int? = null,
    supportingText: String = "",
    readOnly: Boolean = false,
    enabled: Boolean = true,
    keyBoardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit
) {
        TextField(
            modifier = modifier,
            value = value,
            label = { Text(text = label) },
            onValueChange = { onValueChange(it) },
            leadingIcon = {
                if (icon != null)
                    CustomIcon(
                        icon = icon
                    )
            },
            supportingText = {
                if (supportingText.isNotEmpty())
                    Text(
                        text = supportingText,
                        color = MaterialTheme.colorScheme.error
                    )
            },
            enabled = enabled,
            readOnly = readOnly,
            shape = RoundedCornerShape(13.dp),
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyBoardType),
            visualTransformation =
                if (keyBoardType == KeyboardType.Password) PasswordVisualTransformation()
                 else VisualTransformation.None
        )

}

@Preview()
@Composable
fun PreviewCustomTextField() {
    CasualAppTheme {
        CustomTextField(
            modifier = Modifier.fillMaxWidth(),
            value = "Hola que tal",
            label = "Name",
            icon = R.drawable.round_filter_list_24,
            supportingText = "Error message",
            onValueChange = {}
        )

    }
}