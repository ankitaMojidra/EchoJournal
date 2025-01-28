package com.example.echojournal.ui.screens.Components.HomeScreenComponents

import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.echojournal.R
import com.example.echojournal.database.AudioRecord
import com.example.echojournal.getRelativeDay
import com.example.echojournal.ui.theme.EchoJournalTheme
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RecordHistoryItem(record: AudioRecord, onPlay: () -> Unit) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    val mediaPlayer = remember { MediaPlayer() }

    fun playAudio(audioData: ByteArray) {
        try {
            val tempFile = File.createTempFile("audio", ".mp3", context.cacheDir)
            tempFile.deleteOnExit()
            val fos = FileOutputStream(tempFile)
            fos.write(audioData)
            fos.close()

            mediaPlayer.reset()
            mediaPlayer.setDataSource(tempFile.absolutePath)
            mediaPlayer.prepare()
            mediaPlayer.start()
            isPlaying = true
            mediaPlayer.setOnCompletionListener {
                isPlaying = false
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Error playing audio", Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {

        val day = getRelativeDay(record.timestamp)
        Text(text = day)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
        ) {
            when (record.mood) {
                "Stressed" -> Image(
                    painter = painterResource(R.drawable.mood_stressed),
                    contentDescription = "mood"
                )

                " Sad" -> Image(
                    painter = painterResource(R.drawable.mood_sad),
                    contentDescription = "mood"
                )

                "Neutral" -> Image(
                    painter = painterResource(R.drawable.mood_neatral),
                    contentDescription = "mood"
                )

                "Peaceful" -> Image(
                    painter = painterResource(R.drawable.mood_peaceful),
                    contentDescription = "mood"
                )

                "Excited" -> Image(
                    painter = painterResource(R.drawable.mood_exited),
                    contentDescription = "mood"
                )
            }

            Spacer(modifier = Modifier.width(10.dp))
            Card(colors = CardDefaults.cardColors(containerColor = colorResource(R.color.white))) {
                Column(
                    Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = record.title)
                        val timestamp1 = record.timestamp
                        val formattedDate =
                            SimpleDateFormat(
                                "hh:mm a",
                                Locale.getDefault()
                            ).format(Date(timestamp1))
                        Text(text = formattedDate)
                    }

                    val playIconColor = getPlayIconColorForMood(record.mood)
                    val backgroundColor = getBackgroundColorForMood(record.mood)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(shape = RoundedCornerShape(50))
                            .background(color = backgroundColor)
                            .padding(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Play Button
                        IconButton(
                            onClick = { /*TODO*/ },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.White)
                                .size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = "Play",
                                tint = playIconColor
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))

                        // Progress Bar
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                            ) {
                                // Background track
                                drawRoundRect(
                                    color = Color(0xFFE6C9EA), // Light gray/purple color for the track
                                    cornerRadius = CornerRadius(4.dp.toPx())
                                )
                                // Progress indicator
                                drawRoundRect(
                                    color = Color(0xFFD6AEDD), // More saturated purple for the progress
                                    cornerRadius = CornerRadius(4.dp.toPx()),
                                    size = size.copy(width = size.width * 0.3f) // Example progress, make it dynamic
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        val formattedDuration =
                            com.example.echojournal.formatDuration(record.duration)
                        Log.d("Duration::::::::::", "Duration::::::${record.duration}")
                        Log.d("Duration::::::::::", "FormattedDuration::::::$formattedDuration")
                        Text(
                            text = "0:00/$formattedDuration",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorResource(R.color.audio_time),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    Text(text = record.description)
                    TopicTags(record.selectedTopic)
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }
}

@Composable
private fun getBackgroundColorForMood(mood: String): Color {
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
private fun getPlayIconColorForMood(mood: String): Color {
    return when (mood) {
        "Stressed" -> colorResource(R.color.stressed_play_icon)
        "Sad" -> colorResource(R.color.sad_play_icon)
        "Neutral" -> colorResource(R.color.neutral_pay_icon)
        "Peaceful" -> colorResource(R.color.peaceful_play_icon)
        "Excited" -> colorResource(R.color.exited_play_icon)
        else -> Color.Transparent // Default color if mood is not recognized
    }
}

@Preview
@Composable
fun RecordHistoryItemPreview() {
    EchoJournalTheme {
    }
}