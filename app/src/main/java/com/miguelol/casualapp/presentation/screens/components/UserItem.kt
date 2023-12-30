package com.miguelol.casualapp.presentation.screens.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.miguelol.casualapp.domain.model.User
import com.miguelol.casualapp.domain.model.UserPreview
import com.miguelol.casualapp.presentation.theme.CasualAppTheme
import com.miguelol.casualapp.presentation.theme.PowderBlue

@Composable
fun UserItem(
    user: User,
    onClick: (String) -> Unit
) {
    Column(modifier = Modifier.clickable { onClick(user.uid) }) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(64.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CustomAsyncImage(
                image = user.image,
                progressIndicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column() {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "@${user.username}",
                    style = MaterialTheme.typography.titleSmall,
                    color = PowderBlue,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

}

@Preview
@Composable
fun PreviewUserItem(){
    CasualAppTheme {
        UserPreviewItem(
            user = UserPreview(
                name = "Miguel Nunez",
                username = "miguelol99"
            ),
            onClick = {}
        )
    }
}