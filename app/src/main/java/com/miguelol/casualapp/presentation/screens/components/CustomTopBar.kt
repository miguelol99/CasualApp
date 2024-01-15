package com.miguelol.casualapp.presentation.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.miguelol.casualapp.R
import com.miguelol.casualapp.presentation.screens.components.CustomIcon
import com.miguelol.casualapp.presentation.theme.CasualAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    navigateBack: Boolean = false,
    onNavigateBack: () -> Unit = {},
    actions: @Composable  (RowScope.() -> Unit) = {},
) {

    TopAppBar(
        modifier = modifier,
        title = { if (title != null) Text(text = title) },
        navigationIcon = {
            if (navigateBack)
                IconButton( onClick = { onNavigateBack() }) {
                    CustomIcon(icon = R.drawable.round_arrow_back_24, contentDescription = null)
                }
        },
        actions = actions
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewCustomTopBar() {
    CasualAppTheme {
        CustomTopBar(
            navigateBack = true,
            actions = {
                CustomSearchField(
                    modifier = Modifier.fillMaxWidth(0.85f).padding(top = 4.dp),
                    value = "",
                    icon = R.drawable.round_search_24,
                    placeholder = "Search",
                    onValueChange = {}
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCustomTopBar2() {
    CasualAppTheme {
        CustomTopBar(
            title = "Miguel",
            navigateBack = true,
            actions = {}
        )
    }
}