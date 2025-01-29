package com.example.echojournal.ui.screens.Components.HomeScreenComponents

import android.media.MediaPlayer
import android.os.Build
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.echojournal.R
import com.example.echojournal.database.AudioRecord
import com.example.echojournal.formatDuration
import com.example.echojournal.getDate
import com.example.echojournal.getRelativeDay
import com.example.echojournal.ui.screens.Components.getBackgroundColorForMood
import com.example.echojournal.ui.screens.Components.getPlayIconColorForMood
import com.example.echojournal.ui.theme.EchoJournalTheme
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RecordHistoryItem(
    record: AudioRecord,
    previousDayTimestamp: Long?,
    showSeparatorLine: Boolean,
    onPlay: () -> Unit
) {
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
        // Only show the day text if it's different from the previous item
        if (previousDayTimestamp == null ||
            getRelativeDay(previousDayTimestamp) != getRelativeDay(record.timestamp)
        ) {
            Text(
                text = getRelativeDay(record.timestamp),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
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
                            modifier = Modifier.size(40.dp)
                        )

                        " Sad" -> Image(
                            painter = painterResource(R.drawable.mood_sad),
                            contentDescription = "mood",
                            modifier = Modifier.size(40.dp)
                        )

                        "Neutral" -> Image(
                            painter = painterResource(R.drawable.mood_neatral),
                            contentDescription = "mood",
                            modifier = Modifier.size(40.dp)
                        )

                        "Peaceful" -> Image(
                            painter = painterResource(R.drawable.mood_peaceful),
                            contentDescription = "mood",
                            modifier = Modifier.size(40.dp)
                        )

                        "Excited" -> Image(
                            painter = painterResource(R.drawable.mood_exited),
                            contentDescription = "mood",
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    // Separator line
                    if (showSeparatorLine) {
                        Spacer(
                            modifier = Modifier
                                .width(2.dp)
                                .height(180.dp)
                                .background(Color(0xFFE0E0E0))
                        )
                    }
                }
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
                        Text(
                            text = record.title,
                            color = colorResource(R.color.your_echo_general),
                            fontWeight = FontWeight.SemiBold
                        )
                        val timestamp1 = record.timestamp
                        val formattedDate = getDate(timestamp1)
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
                                .clip(CircleShape)
                                .background(Color.White)
                                .size(40.dp)
                        ) {
                            Icon(
                                painter = rememberVectorPainter(
                                    if (isPlaying) {
                                        ImageVector.vectorResource(id = R.drawable.icon_pause)
                                    } else {
                                        ImageVector.vectorResource(id = R.drawable.audio_play)
                                    }
                                ),
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = playIconColor,
                                modifier = Modifier.size(45.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                            ) {
                                drawRoundRect(
                                    color = Color(0xFFE6C9EA),
                                    cornerRadius = CornerRadius(4.dp.toPx())
                                )
                                drawRoundRect(
                                    color = Color(0xFFD6AEDD),
                                    cornerRadius = CornerRadius(4.dp.toPx()),
                                    size = size.copy(width = size.width * 0.3f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        val formattedDuration = formatDuration(record.duration)
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

@Preview
@Composable
fun RecordHistoryItemPreview() {
    EchoJournalTheme {
    }
}
