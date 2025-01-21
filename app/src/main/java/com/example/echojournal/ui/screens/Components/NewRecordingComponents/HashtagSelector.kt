package com.example.echojournal.ui.screens.Components.NewRecordingComponents

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HashtagSelector(
    selectedTags: List<String>,
    onTagAdd: (String) -> Unit,
    onTagRemove: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchText by remember { mutableStateOf("") }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    // Predefined tags list
    val predefinedTags = remember {
        listOf("Work", "Love", "Family", "Friends", "Health", "Travel", "Food", "Sports")
    }

    // Filtered tags based on search
    val filteredTags = remember(searchText) {
        if (searchText.isEmpty()) {
            predefinedTags
        } else {
            predefinedTags.filter { it.lowercase().contains(searchText.lowercase()) }
        }
    }

    Column(modifier = modifier.fillMaxWidth().wrapContentHeight()) {
        // Selected Tags
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.Start,
            maxItemsInEachRow = Int.MAX_VALUE
        ) {
            selectedTags.forEach { tag ->
                TagChip(
                    tag = tag,
                    onRemove = { onTagRemove(tag) },
                    modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
                )
            }
        }

        // Search TextField with Dropdown
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    isDropdownExpanded = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                placeholder = { Text("Add tag...") },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp)
            )

            DropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(horizontal = 16.dp),
                properties = PopupProperties(focusable = false)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                        .heightIn(max = 300.dp)
                ) {
                    items(filteredTags) { tag ->
                        DropdownMenuItem(
                            text = { Text("# $tag") },
                            onClick = {
                                onTagAdd(tag)
                                searchText = ""
                                isDropdownExpanded = false
                            }
                        )
                    }

                    // "Create new tag" option if search text isn't empty
                    if (searchText.isNotEmpty() && !filteredTags.contains(searchText)) {
                        item {
                            DropdownMenuItem(
                                text = { Text("Create '$searchText'") },
                                onClick = {
                                    onTagAdd(searchText)
                                    searchText = ""
                                    isDropdownExpanded = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Create tag"
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TagChip(
    tag: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "# $tag",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove tag",
                modifier = Modifier
                    .size(18.dp)
                    .clickable(onClick = onRemove),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}