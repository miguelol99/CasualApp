package com.miguelol.casualapp.presentation.screens.editprofile.components

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.miguelol.casualapp.R
import com.miguelol.casualapp.presentation.components.CustomTextBox
import com.miguelol.casualapp.presentation.components.CustomTextField
import com.miguelol.casualapp.presentation.screens.components.EditImage
import com.miguelol.casualapp.presentation.screens.editprofile.EditProfileUiState
import com.miguelol.casualapp.presentation.theme.CasualAppTheme
import java.util.Calendar

@Composable
fun EditProfileContent(
    padding: PaddingValues,
    uiState: EditProfileUiState,
    onUsernameInput: (String) -> Unit,
    onNameInput: (String) -> Unit,
    onDescriptionInput: (String) -> Unit,
    onDateOfBirthInput: (String) -> Unit,
    onImageInput: (String) -> Unit
    ) {

    //DATE PICKER
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EditImage(
            modifier = Modifier.size(120.dp),
            imageUrl = uiState.user.image,
            onClick = onImageInput
        )
        Spacer(modifier = Modifier.height(16.dp))
        CustomTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.user.username,
            label = "Username",
            icon = R.drawable.baseline_account_circle_24,
            supportingText = uiState.usernameError,
            onValueChange = { onUsernameInput(it) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.user.name,
            label = "Name",
            icon = R.drawable.round_person_24,
            supportingText = uiState.nameError,
            onValueChange = { onNameInput(it) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                        val calendar = Calendar.getInstance()
                        val datePicker = DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                onDateOfBirthInput("$day/${month+1}/$year")
                            },
                            calendar[Calendar.YEAR],
                            calendar[Calendar.MONTH],
                            calendar[Calendar.DAY_OF_MONTH]
                        )
                        datePicker.datePicker.maxDate = calendar.timeInMillis
                        datePicker.show()
                },
            value = uiState.user.age,
            label = "Date of Birth",
            icon = R.drawable.round_edit_calendar_24,
            supportingText = uiState.birthDateError,
            enabled = false,
            onValueChange = {}
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextBox(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.user.description,
            label = "Description",
            icon = R.drawable.round_description_24,
            supportingText = uiState.descriptionError
        ) { onDescriptionInput(it) }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewEditProfileContent() {
    CasualAppTheme {
        EditProfileContent(
            padding = PaddingValues(),
            uiState = EditProfileUiState(),
            onUsernameInput = {},
            onNameInput = {},
            onDescriptionInput = {},
            onDateOfBirthInput = {},
            onImageInput = {}
        )
    }
}