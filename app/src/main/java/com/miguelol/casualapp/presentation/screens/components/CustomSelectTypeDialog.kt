package com.miguelol.casualapp.presentation.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.miguelol.casual.presentation.components.CustomSegmentedControl
import com.miguelol.casualapp.domain.model.PlanType
import com.miguelol.casualapp.presentation.theme.CasualAppTheme
import com.miguelol.casualapp.utils.Constants.PUBLIC

@Composable
fun CustomSelectTypeDialog(
    onDismissRequest: () -> Unit,
    onItemSelection: (PlanType) -> Unit,
    initialState: PlanType
) {

    val selectedType = remember { mutableStateOf(PlanType.PUBLIC) }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.weight(1f))
                CustomSegmentedControl(
                    onItemSelection = { selectedType.value = it },
                    initState = initialState
                )
                Spacer(modifier = Modifier.weight(1f))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = {
                            onItemSelection(selectedType.value)
                            onDismissRequest()
                        }
                    ) {
                        Text(text = "Confirm")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    TextButton(onClick = { onDismissRequest() }) {
                        Text(text = "Cancel")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Preview
@Composable
fun PreviewCustomSelectTypeDialog() {
    CasualAppTheme {
        CustomSelectTypeDialog(
            onDismissRequest = { },
            onItemSelection = {},
            initialState = PlanType.PUBLIC
        )
    }
}