package com.miguelol.casualapp.presentation.screens.components

import android.app.DatePickerDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar

@Composable
fun CustomDatePicker(
    onDateSelected: (String) -> Unit
) {
    //DATE PICKER
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val selectedDateText = remember { mutableStateOf("") }
    val year = calendar[Calendar.YEAR]
    val month = calendar[Calendar.MONTH]
    val day = calendar[Calendar.DAY_OF_MONTH]
    val datePicker = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, dayOfMonth ->
            selectedDateText.value = "$dayOfMonth/${selectedMonth + 1}/$selectedYear"
            onDateSelected(selectedDateText.value)
        }, year, month, day
    )
    datePicker.datePicker.minDate = calendar.timeInMillis
    datePicker.show()
}