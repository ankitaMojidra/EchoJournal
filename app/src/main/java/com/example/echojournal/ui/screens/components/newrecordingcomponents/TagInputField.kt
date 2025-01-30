package com.example.echojournal.ui.screens.components.newrecordingcomponents

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagInputField(
    existingTags: List<String>,
    onTagSelected: (String) -> Unit,
    onTagCreated: (String) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    val filteredTags = existingTags.filter { it.contains(searchText, ignoreCase = true) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Add Topic") },
            trailingIcon = {
                if (searchText.isNotEmpty()) {
                    IconButton(onClick = { searchText = "" }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Clear")
                    }
                }
            },
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
            modifier = androidx.compose.ui.Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            filteredTags.forEach { tag ->
                DropdownMenuItem(
                    text = { Text("# $tag") },
                    onClick = {
                        onTagSelected(tag)
                        searchText = ""
                        expanded = false
                        keyboardController?.hide()
                    }
                )
            }
            if (searchText.isNotBlank() && filteredTags.none {
                    it.equals(
                        searchText,
                        ignoreCase = true
                    )
                }) {
                DropdownMenuItem(
                    text = { Text("+ Create '$searchText'") },
                    onClick = {
                        onTagCreated(searchText)
                        searchText = ""
                        expanded = false
                        keyboardController?.hide()
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TagInputFieldPreview() {
    val tags = remember {
        mutableStateOf(
            listOf(
                "Work",
                "Love",
                "Community",
                "Passion",
                "Desire",
                "Jane"
            )
        )
    }
    TagInputField(
        existingTags = tags.value,
        onTagSelected = { println("Selected tag: $it") },
        onTagCreated = { println("Created tag: $it") }
    )
}