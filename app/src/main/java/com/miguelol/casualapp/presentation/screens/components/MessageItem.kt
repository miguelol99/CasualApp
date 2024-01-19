package com.miguelol.casualapp.presentation.screens.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.miguelol.casualapp.domain.model.Message
import com.miguelol.casualapp.domain.model.UserPreview
import com.miguelol.casualapp.presentation.theme.CasualAppTheme
import com.miguelol.casualapp.presentation.theme.GambageDark
import java.text.SimpleDateFormat
import java.util.Locale

fun formatTimestampToTime(timestamp: Timestamp): String {
    val date = timestamp.toDate()
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    return formatter.format(date)
}

@Composable
fun MessageItem(
    message: Message,
    isMine: Boolean
) {

    val formattedTime = remember(message.timestamp) { formatTimestampToTime(message.timestamp!!) }

    when(isMine){
        false ->
            Card(
                modifier = Modifier.padding(start = 10.dp, end = 30.dp),
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 10.dp,
                    bottomStart = 10.dp,
                    bottomEnd = 10.dp
                )
            ) {
                Box(
                    contentAlignment = Alignment.BottomEnd
                ){
                    Column(modifier = Modifier.padding(vertical = 4.dp, horizontal = 10.dp)){
                        Text(
                            text = message.fromUser.username,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = message.message)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    Text(
                        modifier = Modifier.padding(end = 10.dp, bottom = 4.dp),
                        text = formattedTime,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        textAlign = TextAlign.End
                    )
                }

            }
        true -> {
            Box(modifier = Modifier.fillMaxWidth()){
                Card(
                    modifier = Modifier
                        .padding(start = 30.dp, end = 10.dp)
                        .align(Alignment.CenterEnd),
                    shape = RoundedCornerShape(
                        topStart = 10.dp,
                        topEnd = 0.dp,
                        bottomStart = 10.dp,
                        bottomEnd = 10.dp
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = GambageDark,
                        contentColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 10.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = message.message
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = formattedTime,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.outlineVariant,
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewMessageItem() {
    CasualAppTheme {
        MessageItem(
            message = Message(
                id = "",
                timestamp = Timestamp.now(),
                fromUser = UserPreview(username = "marianito"),
                message = "AAA"
            ),
            isMine = false
        )
    }
}

@Preview
@Composable
fun PreviewMessageItem2() {
    CasualAppTheme {
        MessageItem(
            message = Message(
                id = "",
                timestamp = Timestamp.now(),
                fromUser = UserPreview(username = "marianito"),
                message = "AAAAAAAAA"
            ),
            isMine = true
        )
    }
}