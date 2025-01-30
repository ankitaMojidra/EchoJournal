package com.example.echojournal.ui.screens.components.homescreencomponents

import android.media.MediaPlayer
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.echojournal.R
import com.example.echojournal.database.AudioRecord
import com.example.echojournal.formatDuration
import com.example.echojournal.getDate
import com.example.echojournal.getRelativeDay
import com.example.echojournal.ui.screens.components.getBackgroundColorForMood
import com.example.echojournal.ui.screens.components.getPlayIconColorForMood
import com.example.echojournal.ui.theme.EchoJournalTheme
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RecordHistoryItem(
    record: AudioRecord,
    showSeparatorLine: Boolean,
    isFirstRecordOfDay: Boolean,
    filteredAudioRecords: List<AudioRecord>,
    index: Int,
    onPlay: () -> Unit
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    val mediaPlayer = remember { MediaPlayer() }
    var currentPosition by remember { mutableIntStateOf(0) }
    val sliderPosition = remember { mutableFloatStateOf(0f) }

    // Function to update the current playback time
    fun updatePlaybackTime() {
        if (mediaPlayer.isPlaying) {
            currentPosition = mediaPlayer.currentPosition
            val progress = currentPosition.toFloat() / record.duration.toFloat()
            sliderPosition.floatValue = progress.coerceIn(0f, 1f)
        }
    }

    // Observe playback progress using LaunchedEffect
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            updatePlaybackTime()
            delay(200) // Update every 200ms
        }
    }

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
                currentPosition = 0
                sliderPosition.floatValue = 0f
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Error playing audio", Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {
        // Only show the day text if it's different from the previous item
        if (index == 0 ||
            getRelativeDay(filteredAudioRecords[index - 1].timestamp) != getRelativeDay(record.timestamp)
        ) {
            Text(
                text = getRelativeDay(record.timestamp),
                modifier = Modifier.padding(bottom = 8.dp),
                fontSize = 13.sp
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.width(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    // Mood emoji
                    when (record.mood) {
                        "Stressed" -> Image(
                            painter = painterResource(R.drawable.mood_stressed),
                            contentDescription = "mood",
                            modifier = Modifier.size(32.dp)
                        )

                        "Sad" -> Image(
                            painter = painterResource(R.drawable.mood_sad),
                            contentDescription = "mood",
                            modifier = Modifier.size(32.dp)
                        )

                        "Neutral" -> Image(
                            painter = painterResource(R.drawable.mood_neatral),
                            contentDescription = "mood",
                            modifier = Modifier.size(32.dp)
                        )

                        "Peaceful" -> Image(
                            painter = painterResource(R.drawable.mood_peaceful),
                            contentDescription = "mood",
                            modifier = Modifier.size(32.dp)
                        )

                        "Excited" -> Image(
                            painter = painterResource(R.drawable.mood_exited),
                            contentDescription = "mood",
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Separator line
                    if (showSeparatorLine) {
                        Spacer(
                            modifier = Modifier
                                .width(2.dp)
                                .height(115.dp)
                                .background(Color(0xFFE0E0E0))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Card(colors = CardDefaults.cardColors(containerColor = colorResource(R.color.white))) {
                Column(
                    Modifier
                        .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = record.title,
                            color = colorResource(R.color.your_echo_general),
                            fontWeight = FontWeight.SemiBold
                        )
                        val timestamp1 = record.timestamp
                        val formattedDate = getDate(timestamp1)
                        Text(text = formattedDate, fontSize = 13.sp)
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
                        IconButton(
                            onClick = {
                                if (!isPlaying) {
                                    playAudio(record.audioData)
                                    onPlay()
                                } else {
                                    mediaPlayer.stop()
                                    isPlaying = false
                                }
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .shadow(elevation = 8.dp, shape = RoundedCornerShape(50.dp))
                                .clip(RoundedCornerShape(50.dp))
                                .background(Color.White)
                        ) {
                            if (isPlaying) {
                                Icon(
                                    imageVector = Icons.Filled.Pause,
                                    contentDescription = "Play",
                                    tint = playIconColor
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = playIconColor
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))

                        Slider(
                            value = sliderPosition.floatValue,
                            onValueChange = { sliderPosition.floatValue = it },
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp), // Adjust height here
                            colors = SliderDefaults.colors(
                                thumbColor = Color.Transparent,
                                activeTrackColor = getPlayIconColorForMood(record.mood),
                                inactiveTrackColor = getPlayIconColorForMood(record.mood).copy(alpha = 0.2f)
                            ),
                            thumb = {
                                SliderDefaults.Thumb(
                                    interactionSource = remember { MutableInteractionSource() },
                                    colors = SliderDefaults.colors(thumbColor = Color.Transparent),
                                    modifier = Modifier.size(0.dp)
                                )
                            },
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        val formattedDuration = formatDuration(record.duration)
                        Text(
                            text = "${formatDuration(currentPosition.toLong())}/$formattedDuration",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorResource(R.color.audio_time),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(text = record.description, fontSize = 13.sp)
                    Spacer(Modifier.height(2.dp))
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


@Preview
@Composable
fun RecordHistoryItemPreview() {
    EchoJournalTheme {
    }
}
