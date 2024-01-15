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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.miguelol.casualapp.R
import com.miguelol.casualapp.domain.model.PlanType
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

    //DATE PICKER
    val context = LocalContext.current

    var dialogState by remember { mutableStateOf(false) }
    if (dialogState)
        CustomSelectTypeDialog(
            onDismissRequest = { dialogState = false },
            onItemSelection = { onEvent(CreatePlanEvents.OnTypeInput(it)) },
            initialState = uiState.inputs.type
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
            imageUrl = uiState.inputs.image,
            onClick = { onEvent(CreatePlanEvents.OnImageInput(it)) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        CustomTextField(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { dialogState = true },
            value = uiState.inputs.type.toString(),
            supportingText = uiState.errors.typeError,
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
                    .clickable {
                        val calendar = Calendar.getInstance()
                        val datePicker = DatePickerDialog(
                            context,
                            { _, year, month, day ->
                                onEvent(CreatePlanEvents.OnDateInput("$day/${month+1}/$year"))
                            },
                            calendar[Calendar.YEAR],
                            calendar[Calendar.MONTH],
                            calendar[Calendar.DAY_OF_MONTH]
                        )
                        datePicker.datePicker.minDate = calendar.timeInMillis
                        datePicker.show()
                   },
                value = uiState.inputs.date,
                supportingText = uiState.errors.dateError,
                label = "Date",
                icon = R.drawable.round_edit_calendar_24,
                enabled = false,
                onValueChange = {  }
            )
            Spacer(modifier = Modifier.width(10.dp))
            CustomTextField(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        //TIME PICKER
                        val calendar = Calendar.getInstance()
                        val timePicker = TimePickerDialog(
                            context,
                            { _, hour, minutes ->
                                onEvent(CreatePlanEvents.OnTimeInput("$hour:$minutes"))
                            }, calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE], false
                        )
                        timePicker.show()
                   },
                value = uiState.inputs.time,
                supportingText = uiState.errors.timeError,
                label = "Time",
                icon = R.drawable.outline_access_time_24,
                enabled = false,
                onValueChange = {}
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.inputs.title,
            supportingText = uiState.errors.titleError,
            label = "Title",
            icon = R.drawable.round_sports_bar_24,
            onValueChange = { onEvent(CreatePlanEvents.OnTitleInput(it)) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.inputs.location,
            supportingText = uiState.errors.locationError,
            label = "Location",
            icon = R.drawable.round_location_on_24,
            onValueChange = { onEvent(CreatePlanEvents.OnLocationInput(it)) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextBox(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.inputs.description,
            supportingText = uiState.errors.descriptionError,
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