package com.example.echojournal.ui.screens.components.homescreencomponents

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.example.echojournal.R

@Composable
fun MoodDropDownMenu(
    onDismiss: () -> Unit,
    selectedMoods: Set<String>,
    updateSelectedMoods: (Set<String>) -> Unit,
) {
    val context = LocalContext.current
    val moodItems = getMoodItems(context)
    val moodOptions = remember { mutableStateOf(moodItems) }

    Log.d("MoodDropDownMenu", "MoodDropDownMenu : selectedMoods = $selectedMoods")

    DropdownMenu(
        expanded = true,
        onDismissRequest = {
            onDismiss()
        },
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp)
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(16.dp))
            .background(color = Color.White)
    ) {
        moodOptions.value.forEach { moodItem ->
            var isItemSelected by remember { mutableStateOf(moodItem.name in selectedMoods) }
            DropdownMenuItem(
                onClick = {
                    val newSelectedMoods = if (moodItem.name in selectedMoods)
                        selectedMoods - moodItem.name
                    else
                        selectedMoods + moodItem.name
                    Log.d(
                        "MoodDropDownMenu",
                        "DropdownMenuItem : newSelectedMoods = $newSelectedMoods"
                    )
                    // **Important: Create a new Set to update the state**
                    updateSelectedMoods(newSelectedMoods.toSet())
                    isItemSelected = !isItemSelected
                    onDismiss()
                },
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp, top = 5.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isItemSelected) colorResource(R.color.selected_row) else Color.White),
                text = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween // Removed spacer
                    ) {
                        Row( // Added nested Row
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            moodItem.icon?.let { icon ->
                                Image(
                                    bitmap = icon.toBitmap().asImageBitmap(),
                                    contentDescription = moodItem.name,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .padding(end = 8.dp)
                                )
                            }
                            Text(moodItem.name, color = colorResource(R.color.all_mood))
                        }
                        if (isItemSelected) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Selected",
                                tint = colorResource(R.color.cancel_color)
                            )
                        }
                    }
                }
            )
        }
    }
}


