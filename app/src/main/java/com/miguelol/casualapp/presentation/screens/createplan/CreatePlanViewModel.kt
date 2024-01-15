package com.miguelol.casualapp.presentation.screens.createplan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.miguelol.casualapp.domain.model.Plan
import com.miguelol.casualapp.domain.model.PlanType
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.model.UserPreview
import com.miguelol.casualapp.domain.usecases.auth.AuthUseCases
import com.miguelol.casualapp.domain.usecases.plans.PlanUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class CreatePlanUiState(
    val inputs: CreatePlanInputs = CreatePlanInputs(),
    val errors: CreatePlanErrors = CreatePlanErrors(),
    var isLoading: Boolean = false,
    val errorMessage: String?  = null,
    val planCreated: Boolean = false
)

sealed interface CreatePlanEvents {
    data class OnImageInput(val input: String): CreatePlanEvents
    data class OnTypeInput(val input: PlanType): CreatePlanEvents
    data class OnDateInput(val input: String): CreatePlanEvents
    data class OnTimeInput(val input: String): CreatePlanEvents
    data class OnTitleInput(val input: String): CreatePlanEvents
    data class OnLocationInput(val input: String): CreatePlanEvents
    data class OnDescriptionInput(val input: String): CreatePlanEvents
    object OnErrorMessageShown: CreatePlanEvents
    object OnCreate: CreatePlanEvents
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class CreatePlanViewModel @Inject constructor(
    authUseCases: AuthUseCases,
    private val planUseCases: PlanUseCases
) : ViewModel() {

    private val uid = authUseCases.getCurrentUser()?.uid!!

    private val _inputs = MutableStateFlow(CreatePlanInputs())
    private val _errors = _inputs.debounce(500L).mapLatest { checkInputs(it) }
    private val _message = MutableStateFlow<String?>(null)
    private val _isLoading = MutableStateFlow(false)
    private val _planCreated = MutableStateFlow(false)

    val uiState = combine(_inputs, _errors, _message, _isLoading, _planCreated)
    { inputs, errors, message, isLoading, planCreated ->
        CreatePlanUiState(
            inputs = inputs,
            errors = errors,
            errorMessage = message,
            isLoading = isLoading,
            planCreated = planCreated
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = CreatePlanUiState()
    )

    fun onEvent(event: CreatePlanEvents) {
        when (event) {
            is CreatePlanEvents.OnImageInput -> onImageInput(event.input)
            is CreatePlanEvents.OnTypeInput -> onTypeInput(event.input)
            is CreatePlanEvents.OnDateInput -> onDateInput(event.input)
            is CreatePlanEvents.OnTimeInput -> onTimeInput(event.input)
            is CreatePlanEvents.OnLocationInput -> onLocationInput(event.input)
            is CreatePlanEvents.OnTitleInput -> onTitleInput(event.input)
            is CreatePlanEvents.OnDescriptionInput -> onDescriptionInput(event.input)
            is CreatePlanEvents.OnCreate -> onCreate()
            is CreatePlanEvents.OnErrorMessageShown -> _message.update { null }
        }
    }

    private fun onCreate() = viewModelScope.launch {

        if (!uiState.value.errors.areAllEmpty()) {
            _message.update { "There are still errors in some of the fields" }
            return@launch
        }

        if (!uiState.value.inputs.areFilled()){
            _message.update { "All the fields must be filled" }
            return@launch
        }

        _isLoading.update { true }

        val plan: Plan
        uiState.value.inputs.let {
            plan = Plan(
                image = it.image,
                type = it.type,
                title = it.title,
                datetime = parseTimestamp(it.date, it.time),
                location = it.location
            )
        }

        when (val resp = planUseCases.createPlan(plan, uid)) {
            is Error -> {_isLoading.update { false }; _message.update { resp.e.message }}
            is Success -> _planCreated.update { true }
        }
    }


    private fun parseTimestamp(date: String, time: String): Timestamp {
        val dateTimeStr = "$date $time"
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        val dateTime = LocalDateTime.parse(dateTimeStr, formatter)
        val instant = dateTime.atZone(ZoneId.systemDefault()).toInstant()
        return Timestamp(instant.epochSecond, instant.nano)
    }

    private fun checkInputs(inputs: CreatePlanInputs): CreatePlanErrors =
        CreatePlanErrors(
            dateError = checkDate(inputs.date),
            timeError = checkTime(inputs.time),
            locationError = checkLocation(inputs.location),
            titleError = checkTitle(inputs.title),
            descriptionError = checkDescription(inputs.description)
        )

        private fun a() {

        }

    private fun onImageInput(input: String) {
        _inputs.update { it.copy(image = input) }
    }

    private fun onTypeInput(input: PlanType) {
        _inputs.update { it.copy(type = input) }
    }

    private fun onDateInput(input: String) {
        if (input.isEmpty()) return
        val parts = input.split("/")
        val day = parts[0].padStart(2, '0')
        val month = parts[1].padStart(2, '0')
        val year = parts[2].padStart(4, '0')
        _inputs.update { it.copy(date = "$day/$month/$year") }
    }

    private fun checkDate(dateString: String): String {
        if (dateString.isBlank()) return ""
        val date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val currentDate = LocalDate.now()
        return when (date.isAfter(currentDate) || date == currentDate) {
            false -> "Invalid date"
            true -> ""
        }
    }

    private fun onTimeInput(input: String) {
        if (input.isEmpty()) return
        val parts = input.split(":")
        val hours = parts[0].padStart(2, '0')
        val minutes = parts[1].padStart(2, '0')
        _inputs.update { it.copy(time = "$hours:$minutes") }
    }

    private fun checkTime(timeString: String): String {
        if(timeString.isBlank()) return ""
        val time = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"))

        val dateString  = uiState.value.inputs.date
        if (dateString.isBlank()) return ""

        val date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd/MM/yyyy"))

        val isValid = (date > LocalDate.now())
                || (date == LocalDate.now() && time.isAfter(LocalTime.now().plusMinutes(15L)))

        return when (isValid) {
            false -> "Invalid time"
            true -> ""
        }
    }

    private fun onLocationInput(input: String) {
        _inputs.update { it.copy(location = input) }
    }

    private fun checkLocation(location: String): String {
        return when (location.length <= 100) {
            false -> "Must be less than 100 characters"
            true -> ""
        }
    }

    private fun onTitleInput(input: String) {
        _inputs.update { it.copy(title = input) }
    }

    private fun checkTitle(title: String): String {
        return when (title.length <= 100) {
            false -> "Must be less than 100 characters"
            true -> ""
        }
    }

    private fun onDescriptionInput(input: String) {
        _inputs.update { it.copy(description = input) }
    }

    private fun checkDescription(description: String): String {
        return when (description.length <= 500) {
            false -> "Must be less than 500 characters"
            true -> ""
        }
    }
}

data class CreatePlanInputs(
    var image: String = "",
    var type: PlanType = PlanType.PUBLIC,
    var date: String = "",
    var time: String = "",
    var location: String = "",
    var title: String = "",
    var description: String = "",
) {
    fun areFilled(): Boolean {

        return listOf(
            image,
            date,
            time,
            location,
            title
        ).all { it.isNotBlank() }
    }
}
data class CreatePlanErrors(
    val typeError: String = "",
    val dateError: String = "",
    val timeError: String = "",
    val titleError: String = "",
    val locationError: String  = "",
    val descriptionError: String = "",
) {
    fun areAllEmpty(): Boolean {
        return listOf(
            typeError,
            dateError,
            timeError,
            titleError,
            locationError,
            descriptionError
        ).all { it.isBlank() }
    }
}