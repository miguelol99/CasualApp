package com.miguelol.casualapp.presentation.screens.editprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.model.User
import com.miguelol.casualapp.domain.usecases.auth.AuthUseCases
import com.miguelol.casualapp.domain.usecases.users.UserUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class EditProfileUiState(
    val user: User = User(),
    val isLoading: Boolean = false,
    val usernameError: String = "",
    val nameError: String = "",
    val birthDateError: String = "",
    val descriptionError: String = "",
    val errorMessage: String? = null,
    val updated: Boolean = false,
    val signOut: Boolean = false
)

sealed interface EditProfileEvents {
    data class OnUsernameInput(val input: String) : EditProfileEvents
    data class OnNameInput(val input: String) : EditProfileEvents
    data class OnDescriptionInput(val input: String) : EditProfileEvents
    data class OnDateOfBirthInput(val input: String) : EditProfileEvents
    data class OnImageInput(val input: String) : EditProfileEvents
    object OnErrorMessageShown : EditProfileEvents
    object OnSignOut: EditProfileEvents
    object OnSave : EditProfileEvents
}

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    private val userUseCases: UserUseCases
) : ViewModel() {

    private val uid: String = authUseCases.getCurrentUser()?.uid!!

    //private val firstTime: Boolean = checkNotNull(savedStateHandle["firstTime"])
    private var isNewImage: Boolean = false
    private var originalUsername: String = ""

    private var _user = MutableStateFlow<User?>(null)
    private val _error = MutableStateFlow<String?>(null)
    private val _isLoading = MutableStateFlow(false)
    private val _updated = MutableStateFlow(false)

    private var _usernameError = MutableStateFlow("")
    private var _nameError = MutableStateFlow("")
    private var _ageError = MutableStateFlow("")
    private var _descriptionError = MutableStateFlow("")

    private val _signOut = MutableStateFlow(false)

    @OptIn(FlowPreview::class)
    val uiState = combine(
        _user,
        _error,
        _isLoading,
        _updated,
        _usernameError.debounce(500),
        _nameError.debounce(500),
        _ageError.debounce(500),
        _descriptionError.debounce(500),
        _signOut
    ) { values ->
        val user = values[0] as User?
        val error = values[1] as String?
        val isLoading = values[2] as Boolean
        val updated = values[3] as Boolean
        val usernameErr = values[4] as String
        val nameErr = values[5] as String
        val ageErr = values[6] as String
        val descriptionErr = values[7] as String
        val signOut = values[8] as Boolean

        EditProfileUiState(
            user = user ?: User(),
            isLoading = (user == null) || updated || isLoading,
            errorMessage = error,
            usernameError = usernameErr,
            nameError = nameErr,
            birthDateError = ageErr,
            descriptionError = descriptionErr,
            updated = updated,
            signOut = signOut
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = EditProfileUiState(isLoading = true)
    )

    init {
        viewModelScope.launch {
            _user.value = getUser(uid)
            originalUsername = _user.value!!.username
        }
    }

    private suspend fun getUser(uid: String): User {
        return userUseCases.getUser(uid)
            .onEach { if (it is Error) _error.value = it.e.message }
            .filter { it is Success }
            .map { (it as Success).data }
            .first()
    }

    fun onEvent(event: EditProfileEvents) {
        when (event) {
            is EditProfileEvents.OnUsernameInput -> onUsernameInput(event.input)
            is EditProfileEvents.OnDateOfBirthInput -> onDateOfBirthInput(event.input)
            is EditProfileEvents.OnNameInput -> onNameInput(event.input)
            is EditProfileEvents.OnDescriptionInput -> onDescriptionInput(event.input)
            is EditProfileEvents.OnImageInput -> onImageInput(event.input)
            is EditProfileEvents.OnSave -> onSave()
            is EditProfileEvents.OnErrorMessageShown -> _error.update { null }
            is EditProfileEvents.OnSignOut -> _signOut.update { authUseCases.signOut(); true }
        }
    }

    private fun onSave() {

        if (_usernameError.value.isBlank() && _nameError.value.isBlank() &&
            _ageError.value.isBlank() && _descriptionError.value.isBlank()
        ) {
            _isLoading.value = true
            viewModelScope.launch {
                when (val resp = userUseCases.updateUser(_user.value!!, isNewImage, originalUsername)) {
                    is Error -> _error.update { resp.e.message }
                    is Success -> _updated.value = true
                }
                _isLoading.value = false
            }
        }
    }

    private fun onImageInput(input: String) {
        isNewImage = true
        _user.update { it?.copy(image = input) }
    }

    private fun onUsernameInput(input: String) {
        _user.update { it?.copy(username = input) }

        val regex = "^[a-zA-Z0-9_]{3,15}$".toRegex()
        if (!regex.matches(input))
            _usernameError.value = "Must contain between 3-15 alphanumeric characters"
        else
            _usernameError.value = ""
    }

    private fun onNameInput(input: String) {
        _user.update { it?.copy(name = input) }

        val regex = "^[a-zA-Z'\\-\\s]{3,50}$".toRegex()
        if (!regex.matches(input))
            _nameError.value = "Must contain between 3-50 alphabetic characters"
        else
            _nameError.value = ""
    }

    private fun onDateOfBirthInput(input: String) {
        val paddedDate = padString(input)
        _user.update { it?.copy(age = paddedDate) }

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val birthDate = LocalDate.parse(paddedDate, formatter)
        val age = Period.between(birthDate, LocalDate.now()).years
        if (age <= 13)
            _ageError.value = "Minimum allowed age is 13 years old."
        else
            _ageError.value = ""
    }

    private fun padString(input: String): String {
        val parts = input.split("/")
        val day = parts[0].padStart(2, '0')
        val month = parts[1].padStart(2, '0')
        val year = parts[2].padStart(4, '0')
        return "$day/$month/$year"
    }

    private fun onDescriptionInput(input: String) {
        _user.update { it?.copy(description = input) }
        if (input.length >= 500) {
            _descriptionError.value = "Must be less than 500 characters"

        } else {
            _descriptionError.value = ""
        }
    }
}