package com.miguelol.casual.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.miguelol.casualapp.R
import com.miguelol.casualapp.domain.model.PlanType
import com.miguelol.casualapp.presentation.theme.CasualAppTheme
import com.miguelol.casualapp.utils.Constants.PRIVATE
import com.miguelol.casualapp.utils.Constants.PUBLIC
import com.miguelol.casualapp.utils.Constants.SECRET

/**
 * items : list of items to be render
 * defaultSelectedItemIndex : to highlight item by default (Optional)
 * useFixedWidth : set true if you want to set fix width to item (Optional)
 * itemWidth : Provide item width if useFixedWidth is set to true (Optional)
 * cornerRadius : To make control as rounded (Optional)
 * color : Set color to control (Optional)
 * onItemSelection : Get selected item index
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSegmentedControl(
    onItemSelection: (PlanType) -> Unit,
    initState: PlanType
) {

    var state by remember { mutableStateOf(initState) }

    var typeText: String = ""
    var typeExplanation: String = ""
    when (state) {
        PlanType.PUBLIC -> {
            typeText = "Public"
            typeExplanation = "Everyone will see your plan!"
        }
        PlanType.PRIVATE -> {
            typeText = "Private"
            typeExplanation = "Only your friends will see your plan!"
        }
        PlanType.SECRET -> {
            typeText = "Secret"
            typeExplanation = "Only the people you invite will see the plan!"
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            text = typeText
        )
        Text(
            text = typeExplanation,
            style = MaterialTheme.typography.labelLarge
        )
        Spacer(modifier = Modifier.height(5.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            OutlinedButton(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(
                    topStart = 50.dp,
                    bottomStart = 50.dp,
                    topEnd = 0.dp,
                    bottomEnd = 0.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state == PlanType.PUBLIC)
                        MaterialTheme.colorScheme.surfaceVariant
                    else
                        MaterialTheme.colorScheme.surface,

                    contentColor = if (state == PlanType.PUBLIC)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface
                ),
                onClick = {
                    if (state != PlanType.PUBLIC) {
                        state = PlanType.PUBLIC
                        onItemSelection(state)
                    }
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.round_public_24),
                    contentDescription = ""
                )
            }
            OutlinedButton(
                modifier = Modifier.weight(1f),
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state == PlanType.PRIVATE)
                        MaterialTheme.colorScheme.surfaceVariant
                    else
                        MaterialTheme.colorScheme.surface,
                    contentColor = if (state == PlanType.PRIVATE)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface
                ),
                onClick = {
                    if (state != PlanType.PRIVATE) {
                        state = PlanType.PRIVATE
                        onItemSelection(state)
                    }
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_shield_24),
                    contentDescription = ""
                )
            }
            OutlinedButton(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(
                    topEnd = 50.dp,
                    bottomEnd = 50.dp,
                    topStart = 0.dp,
                    bottomStart = 0.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state == PlanType.SECRET)
                        MaterialTheme.colorScheme.surfaceVariant
                    else
                        MaterialTheme.colorScheme.surface,
                    contentColor = if (state == PlanType.SECRET)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface
                ),
                onClick = {
                    if (state != PlanType.SECRET) {
                        state = PlanType.SECRET
                        onItemSelection(checkNotNull(state))
                    }
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.round_visibility_off_24),
                    contentDescription = ""
                )
            }
        }
    }


}

@Preview
@Composable
fun PreviewCustomSegmentedControl() {
    CasualAppTheme() {
        CustomSegmentedControl(
            onItemSelection = {},
            initState = PlanType.PRIVATE)
    }
}