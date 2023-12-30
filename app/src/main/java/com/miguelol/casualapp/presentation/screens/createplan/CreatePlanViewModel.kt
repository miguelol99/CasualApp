package com.miguelol.casualapp.presentation.screens.createplan

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.miguelol.casualapp.domain.model.PlanPreview
import com.miguelol.casualapp.domain.model.Response
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.usecases.auth.AuthUseCases
import com.miguelol.casualapp.domain.usecases.plans.PlanUseCases
import com.miguelol.casualapp.domain.usecases.UserUseCases
import com.miguelol.casualapp.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class CreatePlanUiState(
    val image: String = "",
    val type: String = "",
    val date: String = "",
    val time: String = "",
    val title: String = "",
    val location: String = "",
    val description: String = "",

    val typeError: String = "",
    val dateError: String = "",
    val timeError: String = "",
    val titleError: String = "",
    val locationError: String  = "",
    val descriptionError: String = "",

    val isLoading: Boolean = false,
    val errorMessage: String?  = null,
    val flag: Boolean = false
)

sealed interface CreatePlanEvents {
    data class OnImageInput(val input: String): CreatePlanEvents
    data class OnTypeInput(val input: String): CreatePlanEvents
    data class OnDateInput(val input: String): CreatePlanEvents
    data class OnTimeInput(val input: String): CreatePlanEvents
    data class OnTitleInput(val input: String): CreatePlanEvents
    data class OnLocationInput(val input: String): CreatePlanEvents
    data class OnDescriptionInput(val input: String): CreatePlanEvents
    object OnCreate: CreatePlanEvents
}

@HiltViewModel
class CreatePlanViewModel @Inject constructor(
    authUseCases: AuthUseCases,
    private val userUseCases: UserUseCases,
    private val planUseCases: PlanUseCases
) : ViewModel() {

    private val uid = authUseCases.getCurrentUser()?.uid!!

    var uiState: CreatePlanUiState by mutableStateOf(CreatePlanUiState())
    private set

    fun onEvent(event: CreatePlanEvents) {
        when(event){
            is CreatePlanEvents.OnImageInput -> onImageInput(event.input)
            is CreatePlanEvents.OnTypeInput -> onTypeInput(event.input)
            is CreatePlanEvents.OnDateInput -> onDateInput(event.input)
            is CreatePlanEvents.OnTimeInput -> onTimeInput(event.input)
            is CreatePlanEvents.OnTitleInput -> onTitleInput(event.input)
            is CreatePlanEvents.OnLocationInput -> onLocationInput(event.input)
            is CreatePlanEvents.OnDescriptionInput -> onDescriptionInput(event.input)
            is CreatePlanEvents.OnCreate -> onCreate()
        }
    }

    private fun onCreate() {
        viewModelScope.launch {
            if (!allInputsAreValid()) return@launch
            uiState = uiState.copy(isLoading = true)

            val plan = PlanPreview(
                type = uiState.type,
                title = uiState.title,
                datetime = parseTimestamp(uiState.date, uiState.time),
                location = uiState.location,
                //host = username,
                image = uiState.image,
                participants = listOf(uid)
            )

            uiState = when (val response = planUseCases.createPlan(plan, uid)) {
                is Error -> uiState.copy(errorMessage = response.e.message, isLoading = false)
                is Success -> uiState.copy(flag = true)

            }
        }
    }

    private fun parseTimestamp(date: String, time: String): Timestamp {
        val dateTimeStr = "$date $time"
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        val dateTime = LocalDateTime.parse(dateTimeStr, formatter)
        val instant = dateTime.atZone(ZoneId.systemDefault()).toInstant()
        return Timestamp(instant.epochSecond, instant.nano)
    }

    private fun onDescriptionInput(input: String) {
        uiState = uiState.copy(description = input)
    }

    private fun onLocationInput(input: String) {
        uiState = uiState.copy(location = input)
    }

    private fun onTitleInput(input: String) {
        uiState = uiState.copy(title = input)
    }

    private fun onTimeInput(input: String) {
        if (input.isEmpty()) return
        Log.d("TEST", input)
        val parts = input.split(":")
        val hours = parts[0].padStart(2, '0')
        val minutes = parts[1].padStart(2, '0')
        uiState = uiState.copy(time = "$hours:$minutes")
    }

    private fun onDateInput(input: String) {
        if (input.isEmpty()) return
        val parts = input.split("/")
        val day = parts[0].padStart(2, '0')
        val month = parts[1].padStart(2, '0')
        val year = parts[2].padStart(4, '0')
        uiState = uiState.copy(date = "$day/$month/$year")
    }

    private fun onTypeInput(input: String) {
        uiState = uiState.copy(type = input)
    }

    private fun onImageInput(input: String) {
        uiState = uiState.copy(image = input)
    }

    private fun allInputsAreValid(): Boolean {
        val typeValid = checkType()
        val dateValid = checkDate()
        val timeValid = checkTime()
        val titleValid = checkTitle()
        val locationValid = checkLocation()
        val descriptionValid = checkDescription()

        return typeValid && dateValid && timeValid && titleValid && locationValid && descriptionValid
    }

    private fun checkType(): Boolean {
        val isValid = uiState.type in setOf(Constants.PUBLIC, Constants.PRIVATE, Constants.SECRET)
        uiState = when(isValid) {
            false -> uiState.copy(typeError = "Field required.")
            true -> uiState.copy(typeError = "")
        }
        return isValid
    }

    private fun checkDate(): Boolean {
        if (uiState.date.isBlank()) {
            uiState = uiState.copy(dateError = "Field required.")
            return false
        }
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val inputDate = LocalDate.parse(uiState.date, formatter)
        val currentDate = LocalDate.now()
        val isValid = inputDate.isAfter(currentDate) || inputDate == currentDate

        uiState = when(isValid) {
            false -> uiState.copy(dateError = "Invalid date")
            true -> uiState.copy(dateError = "")
        }

        return isValid
    }

    private fun checkTime(): Boolean {

        if (uiState.time.isBlank()) {
            uiState = uiState.copy(timeError = "Field required.")
            return false
        }

        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val inputTime = LocalTime.parse(uiState.time, formatter)
        val currentTime = LocalTime.now()
        val isValid = inputTime.isAfter(currentTime)

        uiState = when(isValid) {
            false -> uiState.copy(timeError = "Invalid time.")
            true -> uiState.copy(timeError = "")
        }

        return isValid
    }

    private fun checkTitle(): Boolean {

        if (uiState.title.isBlank()){
            uiState = uiState.copy(titleError = "Field required.")
            return false
        }

        val isValid = uiState.title.length <= 100
        uiState = when(isValid) {
            false -> uiState.copy(titleError = "Must be less than 100 characters.")
            true -> uiState.copy(titleError = "")
        }
        return isValid
    }

    private fun checkLocation(): Boolean {
        val isValid = uiState.location.isNotBlank()
        uiState = when(isValid) {
            false -> uiState.copy(locationError = "Field required.")
            true -> uiState.copy(locationError = "")
        }
        return isValid
    }

    private fun checkDescription(): Boolean {
        val isValid = uiState.description.length <= 500
        uiState = when(isValid) {
            false -> uiState.copy(descriptionError = "Must be less than 500 characters.")
            true -> uiState.copy(descriptionError = "")
        }
        return isValid
    }

}