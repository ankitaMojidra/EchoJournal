package com.example.echojournal.ui.screens.Components.HomeScreenComponents

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.echojournal.model.MoodItem

@Composable
fun MoodDropDownMenu(
    onDismiss: () -> Unit,
    selectedMoods: Set<String>,
    updateSelectedMoods: (Set<String>) -> Unit,
) {
    val context = LocalContext.current
    val moodItems = getMoodItems(context)
    val moodOptions = remember { mutableStateOf(moodItems) }

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
            DropdownMenuItem(onClick = {
                val newSelectedMoods = if (moodItem.name in selectedMoods)
                    selectedMoods - moodItem.name
                else
                    selectedMoods + moodItem.name

                updateSelectedMoods(newSelectedMoods)
               // onDismiss()
            },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        moodItem.icon?.let { icon ->
                            Image(
                                bitmap = icon.toBitmap().asImageBitmap(),
                                contentDescription = moodItem.name,
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(end = 8.dp)
                            )
                        }
                        Text(moodItem.name)
                        if (moodItem.name in selectedMoods) {
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

@Composable
fun getMoodItems(context: Context): List<MoodItem> {
    return listOf(
        MoodItem(context.getString(R.string.exited), context.getDrawable(R.drawable.mood_exited)),
        MoodItem(
            context.getString(R.string.peaceful),
            context.getDrawable(R.drawable.mood_peaceful)
        ),
        MoodItem(context.getString(R.string.neatral), context.getDrawable(R.drawable.mood_neatral)),
        MoodItem(context.getString(R.string.sad), context.getDrawable(R.drawable.mood_sad)),
        MoodItem(
            context.getString(R.string.stressed),
            context.getDrawable(R.drawable.mood_stressed)
        )
    )
}

