package com.miguelol.casualapp.presentation.screens.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.miguelol.casualapp.domain.model.Success
import com.miguelol.casualapp.domain.model.Error
import com.miguelol.casualapp.domain.model.Message
import com.miguelol.casualapp.domain.usecases.auth.AuthUseCases
import com.miguelol.casualapp.domain.usecases.plans.PlanUseCases
import com.miguelol.casualapp.presentation.navigation.DestinationArgs.PLAN_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

data class ChatUiState(
    val messageToSend: String = "",
    val title: String = "",
    val image: String = "",
    val messages: List<Message> = emptyList(),
    val myUid: String = "",
    val isDeleted: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val iAmAParticipant: Boolean = true
)

sealed interface ChatEvents {
    data class OnMessageInput(val input: String): ChatEvents
    object OnMessageReady: ChatEvents
    object OnErrorMessageShown: ChatEvents
}

@HiltViewModel
class ChatViewModel @Inject constructor(
    authUseCases: AuthUseCases,
    private val planUseCases: PlanUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _myUid: String = authUseCases.getCurrentUser()?.uid!!
    private val _planId: String = savedStateHandle[PLAN_ID]!!

    private val _messageToSend = MutableStateFlow("")
    private val _messages = planUseCases.getChat(_planId)
    private val _plan = planUseCases.getPlan(_planId)
    private val _error = MutableStateFlow<String?>(null)
    private val _isLoading = MutableStateFlow(false)

    val uiState = combine(_messageToSend, _plan, _messages, _error, _isLoading)
    { messageToSend, plan, messages, error, isLoading ->
        when {
            plan is Error -> ChatUiState(
                isLoading = true,
                error = plan.e.message,
                iAmAParticipant = false
            )
            messages is Error -> ChatUiState(
                isLoading = true,
                error = messages.e.message,
                iAmAParticipant = false
            )
            else -> {
                ChatUiState(
                    messageToSend = messageToSend,
                    title = (plan as Success).data?.title ?: "",
                    image = plan.data?.image ?: "",
                    messages = (messages as Success).data,
                    myUid = _myUid,
                    isDeleted = plan.data == null,
                    iAmAParticipant = plan.data?.participants?.contains(_myUid) ?: false,
                    error = error
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = ChatUiState(isLoading = true)
    )

    fun onEvent(event: ChatEvents) {
        when (event) {
            is ChatEvents.OnMessageInput -> _messageToSend.update { event.input }
            is ChatEvents.OnMessageReady -> sendMessage()
            ChatEvents.OnErrorMessageShown -> _error.update { null }

        }
    }

    private fun sendMessage() {
        val text = uiState.value.messageToSend
        if (text.isBlank()) return
        viewModelScope.launch {
            when (val resp =
                planUseCases.sendMessage(planId = _planId, text = text, fromUid = _myUid)) {
                is Error -> _error.update { resp.e.message }
                is Success -> _messageToSend.update { "" }
            }
        }
    }

}