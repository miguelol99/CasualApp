package com.miguelol.casualapp.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.miguelol.casualapp.R
import com.miguelol.casualapp.presentation.theme.CasualAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextBox(
    modifier: Modifier,
    value: String,
    label: String,
    icon: Int? = null,
    supportingText: String = "",
    onValueChange: (String) -> Unit
) {

    TextField(
        modifier = modifier.height((40*4).dp),
        value = value,
        label = { Text(text = label) },
        leadingIcon = {
            if (icon != null)
                Icon(
                    modifier = Modifier.offset(y = (-52).dp),
                    painter = painterResource(icon),
                    contentDescription = "leading icon"
                )
        },
        supportingText = {
            if (supportingText.isNotEmpty())
                Text(text = supportingText, color = MaterialTheme.colorScheme.error)
        },
        onValueChange = { onValueChange(it) },
        shape = RoundedCornerShape(13.dp),
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        maxLines = 4
    )

}

@Preview()
@Composable
fun PreviewCustomTextBox() {
    CasualAppTheme {
        CustomTextBox(
            modifier = Modifier.fillMaxWidth(),
            value = "Esta es una description",
            label = "Description",
            icon = R.drawable.round_description_24,
            supportingText = "Field required.",
            onValueChange = {}
        )
    }
}