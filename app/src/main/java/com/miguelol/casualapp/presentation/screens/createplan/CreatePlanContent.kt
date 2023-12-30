package com.miguelol.casualapp.presentation.screens.createplan

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.miguelol.casualapp.R
import com.miguelol.casualapp.presentation.components.CustomTextBox
import com.miguelol.casualapp.presentation.components.CustomTextField
import com.miguelol.casualapp.presentation.screens.components.CustomSelectTypeDialog
import com.miguelol.casualapp.presentation.screens.components.EditImage
import com.miguelol.casualapp.presentation.theme.CasualAppTheme
import com.miguelol.casualapp.utils.Constants
import java.util.Calendar

@Composable
fun CreatePlanContent(
    modifier: Modifier,
    uiState: CreatePlanUiState,
    onEvent: (CreatePlanEvents) -> Unit
) {

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    //DATE PICKER
    val selectedDateText = remember { mutableStateOf("") }
    val year = calendar[Calendar.YEAR]
    val month = calendar[Calendar.MONTH]
    val day = calendar[Calendar.DAY_OF_MONTH]
    val datePicker = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, dayOfMonth ->
            selectedDateText.value = "$dayOfMonth/${selectedMonth + 1}/$selectedYear"
            onEvent(CreatePlanEvents.OnDateInput(selectedDateText.value))
        }, year, month, day
    )
    datePicker.datePicker.minDate = calendar.timeInMillis

    //TIME PICKER
    val selectedTimeText = remember { mutableStateOf("") }
    val hour = calendar[Calendar.HOUR_OF_DAY]
    val minute = calendar[Calendar.MINUTE]
    val timePicker = TimePickerDialog(
        context,
        { _, selectedHour, selectedMinute ->
            selectedTimeText.value = "$selectedHour:$selectedMinute"
            onEvent(CreatePlanEvents.OnTimeInput(selectedTimeText.value))
        }, hour, minute, false
    )

    val dialogState = remember { mutableStateOf(false) }
    if (dialogState.value)
        CustomSelectTypeDialog(
            onDismissRequest = { dialogState.value = false },
            onItemSelection = { onEvent(CreatePlanEvents.OnTypeInput(it)) },
            initialState = uiState.type.ifBlank { Constants.PUBLIC }
        )

    Column(
        modifier = modifier
            .padding(top = 8.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
            .verticalScroll(rememberScrollState())
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        EditImage(
            modifier = Modifier.size(120.dp),
            imageUrl = uiState.image,
            onClick = { onEvent(CreatePlanEvents.OnImageInput(it)) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        CustomTextField(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { dialogState.value = true },
            value = uiState.type.uppercase(),
            supportingText = uiState.typeError,
            label = "Type",
            icon = R.drawable.round_public_24,
            enabled = false,
            onValueChange = {}
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            CustomTextField(
                modifier = Modifier
                    .weight(1f)
                    .clickable { datePicker.show() },
                value = uiState.date,
                supportingText = uiState.dateError,
                label = "Date",
                icon = R.drawable.round_edit_calendar_24,
                enabled = false,
                onValueChange = {  }
            )
            Spacer(modifier = Modifier.width(10.dp))
            CustomTextField(
                modifier = Modifier
                    .weight(1f)
                    .clickable { timePicker.show() },
                value = uiState.time,
                supportingText = uiState.timeError,
                label = "Time",
                icon = R.drawable.outline_access_time_24,
                enabled = false,
                onValueChange = {}
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.title,
            supportingText = uiState.titleError,
            label = "Title",
            icon = R.drawable.round_sports_bar_24,
            onValueChange = { onEvent(CreatePlanEvents.OnTitleInput(it)) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.location,
            supportingText = uiState.locationError,
            label = "Location",
            icon = R.drawable.round_location_on_24,
            onValueChange = { onEvent(CreatePlanEvents.OnLocationInput(it)) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextBox(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.description,
            supportingText = uiState.descriptionError,
            label = "Description",
            icon = R.drawable.round_description_24,
            onValueChange = { onEvent(CreatePlanEvents.OnDescriptionInput(it))}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCreatePlanContent() {
    CasualAppTheme {
        CreatePlanContent(
            modifier = Modifier,
            uiState = CreatePlanUiState(),
            onEvent = {}
        )
    }
}