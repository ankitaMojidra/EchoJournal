package com.example.echojournal.ui.screens.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.example.echojournal.R

@Composable
fun getBackgroundColorForMood(mood: String): Color {
    return when (mood) {
        "Stressed" -> colorResource(R.color.stressed_bg)
        "Sad" -> colorResource(R.color.sad_bg)
        "Neutral" -> colorResource(R.color.neatral_bg)
        "Peaceful" -> colorResource(R.color.peaceful_bg)
        "Excited" -> colorResource(R.color.exited_bg)
        else -> Color.Transparent // Default color if mood is not recognized
    }
}

@Composable
fun getPlayIconColorForMood(mood: String): Color {
    return when (mood) {
        "Stressed" -> colorResource(R.color.stressed_play_icon)
        "Sad" -> colorResource(R.color.sad_play_icon)
        "Neutral" -> colorResource(R.color.neutral_pay_icon)
        "Peaceful" -> colorResource(R.color.peaceful_play_icon)
        "Excited" -> colorResource(R.color.exited_play_icon)
        else -> Color.Transparent // Default color if mood is not recognized
    }
}

@Composable
fun getProgressDisableColorForMood(mood: String): Color {
    return when (mood) {
        "Stressed" -> colorResource(R.color.stressed_play_icon)
        "Sad" -> colorResource(R.color.sad_play_icon)
        "Neutral" -> colorResource(R.color.neutral_pay_icon)
        "Peaceful" -> colorResource(R.color.peaceful_play_icon)
        "Excited" -> colorResource(R.color.exited_play_icon)
        else -> Color.Transparent // Default color if mood is not recognized
    }
}