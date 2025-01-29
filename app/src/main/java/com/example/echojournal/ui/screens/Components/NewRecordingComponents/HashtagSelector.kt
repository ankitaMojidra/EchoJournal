package com.example.echojournal.ui.screens.Components.NewRecordingComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HashtagSelector(
    selectedTags: List<String>,
    allTopics: List<String>,
    onTagAdd: (String) -> Unit,
    onTagRemove: (String) -> Unit,
    onDismiss: () -> Unit,  // Add this callback
    modifier: Modifier = Modifier,
    expanded: Boolean = false  // Change initiallyExpanded to expanded
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            selectedTags.forEach { tag ->
                TagChip(
                    text = tag,
                    onRemove = { onTagRemove(tag) },
                    modifier = Modifier.padding(end = 4.dp, bottom = 4.dp)
                )
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = onDismiss,  // Use the onDismiss callback
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clip(RoundedCornerShape(8.dp))
            ) {
                allTopics.forEach { tag ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("#")
                                Text(tag)
                            }
                        },
                        onClick = {
                            onTagAdd(tag)
                            onDismiss()  // Call onDismiss instead of directly setting expanded
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TagChip(
    text: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(32.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            IconButton(
                onClick = onRemove,
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
            ) {
                Icon(
                    Icons.Rounded.Close,
                    contentDescription = "Remove tag",
                    modifier = Modifier.size(12.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}