package com.miguelol.casualapp.presentation.screens.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.miguelol.casualapp.R
import com.miguelol.casualapp.domain.model.Message
import com.miguelol.casualapp.domain.model.UserPreview
import com.miguelol.casualapp.presentation.screens.components.CustomIcon
import com.miguelol.casualapp.presentation.screens.components.MessageItem
import com.miguelol.casualapp.presentation.theme.CasualAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatContent(
    modifier: Modifier,
    uiState: ChatUiState,
    onEvent: (ChatEvents) -> Unit
) {

    val lazyColumnState = rememberLazyListState()
    LaunchedEffect(uiState.messages.size) {
        lazyColumnState.scrollToItem(0)
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            reverseLayout = true,
            state = lazyColumnState
        ) {
            items(
                items = uiState.messages,
                key = {it.id}
            ) {
                MessageItem(message = it, isMine = (it.fromUser.uid == uiState.myUid))
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                modifier = Modifier.weight(1f),
                value = uiState.messageToSend,
                placeholder = { Text(text = "Message") },
                onValueChange = { onEvent(ChatEvents.OnMessageInput(it)) },
                shape = RoundedCornerShape(13.dp),
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done // Set the IME action to Done (typically represents "Enter" on the keyboard)
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onEvent(ChatEvents.OnMessageReady) }
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            FilledIconButton(
                modifier = Modifier.size(54.dp),
                onClick = { onEvent(ChatEvents.OnMessageReady) }
            ) {
                CustomIcon(
                    modifier = Modifier.padding(start = 3.dp),
                    icon = R.drawable.round_send_24)
            }
        }
    }


}

@Preview(showBackground = true)
@Composable
fun PreviewChatContent() {
    CasualAppTheme {
        ChatContent(
            modifier = Modifier,
            uiState = ChatUiState(
                messages = listOf(
                    Message(
                        id = "a",
                        fromUser = UserPreview(username = "marianito"),
                        message = "Holaaaa"
                    ),
                    Message(
                        id = "b",
                        fromUser = UserPreview(uid = "myUid"),
                        message  = "Que tal brooo"
                    )
                ),
                myUid = "myUid",
                iAmAParticipant = false
            ),
            onEvent = {}
        )
    }
}