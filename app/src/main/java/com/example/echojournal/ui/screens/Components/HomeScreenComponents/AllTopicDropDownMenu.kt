package com.example.echojournal.ui.screens.Components.HomeScreenComponents

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.echojournal.R

@Composable
fun AllTopicDropDownMenu(
onDismiss: () -> Unit,
selectedTopics: Set<String>,
updateSelectedTopics: (Set<String>) -> Unit,
topics: List<String>
) {
    DropdownMenu(
        expanded = true,
        onDismissRequest = {onDismiss()},
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp)
            .clip(shape = RoundedCornerShape(16.dp))
            .fillMaxWidth()
            .background(color = Color.White)
    ) {
        topics.forEach { topic ->
            DropdownMenuItem(
                onClick = {
                    val newSelectedTopics = if (topic in selectedTopics)
                        selectedTopics - topic
                    else
                        selectedTopics + topic
                    Log.d("MoodDropDownMenu", "DropdownMenuItem : newSelecteTopics = $newSelectedTopics")
                    updateSelectedTopics(newSelectedTopics.toSet())
                    onDismiss()
                },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(topic)
                        if (topic in selectedTopics) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Selected",
                                tint = colorResource(R.color.all_mood)
                            )
                        }
                    }
                }
            )
        }
    }
}