package com.example.echojournal.ui.screens.Components.NewRecordingComponents

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.echojournal.R
import com.example.echojournal.ui.theme.EchoJournalTheme

@Composable
fun Mood(modifier: Modifier, onCloseBottomSheet: (String?) -> Unit) {

    val context = LocalContext.current
    var selecedMood by remember { mutableStateOf<String?>(null) }

    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = context.getString(R.string.how_are_you_doing))

        Spacer(modifier.height(10.dp))

        Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {

            MoodItem(imageResGray = R.drawable.stressed_gray,
                imageResColor = R.drawable.mood_stressed,
                textRes = R.string.stressed,
                isSelected = selecedMood == "Stressed",
                onMoodSelected = { selecedMood = "Stressed" })

            MoodItem(imageResGray = R.drawable.sad_gray,
                imageResColor = R.drawable.mood_sad,
                textRes = R.string.sad,
                isSelected = selecedMood == "Sad",
                onMoodSelected = { selecedMood = "Sad" })

            MoodItem(imageResGray = R.drawable.neutral_gray,
                imageResColor = R.drawable.mood_neatral,
                textRes = R.string.neatral,
                isSelected = selecedMood == "Neutral",
                onMoodSelected = { selecedMood = "Neutral" })

            MoodItem(imageResGray = R.drawable.peaceful_gray,
                imageResColor = R.drawable.mood_peaceful,
                textRes = R.string.peaceful,
                isSelected = selecedMood == "Peaceful",
                onMoodSelected = {
                    selecedMood = "Peaceful"
                    Log.d("Mood", "Selected mood: $selecedMood")
                })

            MoodItem(imageResGray = R.drawable.excited_gray,
                imageResColor = R.drawable.mood_exited,
                textRes = R.string.exited,
                isSelected = selecedMood == "Excited",
                onMoodSelected = { selecedMood = "Excited" })
        }

        Spacer(modifier.height(10.dp))

        BottomBar(
            modifier = Modifier,
            isConfirmVisible = false,
            isConfirmEnabled = selecedMood!=null,
            onConfirm = {onCloseBottomSheet(selecedMood)},
            onCancel = {onCloseBottomSheet(null)}
        )
    }
}

@Preview
@Composable
fun MoodPreview() {
    EchoJournalTheme {
    }
}

