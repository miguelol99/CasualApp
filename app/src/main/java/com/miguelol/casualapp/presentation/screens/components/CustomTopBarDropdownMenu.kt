package com.miguelol.casualapp.presentation.screens.plans.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.miguelol.casualapp.R
import com.miguelol.casualapp.presentation.theme.CasualAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBarDropdownMenu(
    title: String,
    onAllFilterSelected: () -> Unit,
    onPrivateFilterSelected: () -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(text = title) },
        actions = {
            Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
                IconButton(onClick = { expanded = !expanded}) {
                    Icon(painter = painterResource(R.drawable.round_filter_list_24), contentDescription = "filter icon" )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .wrapContentSize(Alignment.TopEnd)
                ) {
                    DropdownMenuItem(
                        text = { Text(text = "All") },
                        leadingIcon = { Icon(painter = painterResource(id = R.drawable.round_public_24), contentDescription = null)},
                        onClick = { onAllFilterSelected(); expanded = !expanded }
                    )
                    DropdownMenuItem(
                        text = { Text(text = "Private") },
                        leadingIcon = { Icon(painter = painterResource(id = R.drawable.outline_shield_24), contentDescription = null) },
                        onClick = { onPrivateFilterSelected(); expanded = !expanded }
                    )
                }
            }
        }
    )
}

@Preview
@Composable
fun PreviewCustomTopAppBar() {
    CasualAppTheme {
        CustomTopBarDropdownMenu(
            title = "Public Plans",
            onAllFilterSelected = {},
            onPrivateFilterSelected = {}
        )
    }
}