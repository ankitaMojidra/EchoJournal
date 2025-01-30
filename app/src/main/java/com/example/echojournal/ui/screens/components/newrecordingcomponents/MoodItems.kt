package com.example.echojournal.ui.screens.components.newrecordingcomponents

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun MoodItem(
    imageResGray: Int,
    imageResColor: Int,
    textRes: Int,
    isSelected: Boolean,
    onMoodSelected: () -> Unit
) {
    val context = LocalContext.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = if (isSelected) imageResColor else imageResGray),
            contentDescription = context.getString(textRes),
            modifier = Modifier
                .size(40.dp)
                .clickable {
                    Log.d("MoodItem", "Clicked on ${context.getString(textRes)}")

                    onMoodSelected() }
        )
        Text(
            text = context.getString(textRes),
            textAlign = TextAlign.Center,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
