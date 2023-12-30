package com.miguelol.casualapp.presentation.screens.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.miguelol.casualapp.R
import com.miguelol.casualapp.presentation.theme.CasualAppTheme

@Composable
fun EditImage(
    modifier: Modifier,
    imageUrl: String,
    onClick: (String) -> Unit
) {

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) onClick(uri.toString())
        }
    )

    val newModifier = modifier
        .clip(CircleShape)
        .clickable {
            singlePhotoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

    Box(contentAlignment = Alignment.Center) {
        if (imageUrl.isEmpty()) {
            Image(
                modifier = newModifier,
                painter = painterResource(id = R.drawable.image_placeholder),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        } else {
            AsyncImage(
                modifier = newModifier,
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
        Surface(
            modifier = Modifier
                .size(38.dp)
                .offset(x = 40.dp, y = (-40).dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            border = BorderStroke(width = 3.dp, color = MaterialTheme.colorScheme.background)
        ) {
            Box(contentAlignment = Alignment.Center) {
                CustomIcon(
                    modifier = Modifier.size(18.dp),
                    icon = R.drawable.round_edit_24,
                )
            }

        }
    }
}

@Preview
@Composable
fun PreviewEditImage() {
    CasualAppTheme() {
        EditImage(modifier = Modifier.size(120.dp),imageUrl = "", {})
    }
}