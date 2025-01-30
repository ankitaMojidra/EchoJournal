package com.example.echojournal.ui.screens.components.homescreencomponents

import android.content.Context
import androidx.compose.runtime.Composable
import com.example.echojournal.R
import com.example.echojournal.model.MoodItem

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
