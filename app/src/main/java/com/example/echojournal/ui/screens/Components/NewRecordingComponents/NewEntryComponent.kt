package com.example.echojournal.ui.screens.Components.NewRecordingComponents

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.echojournal.Constants
import com.example.echojournal.R
import com.example.echojournal.database.AudioRecord
import com.example.echojournal.database.AudioRecordDao
import com.example.echojournal.database.AudioRecordDatabase
import com.example.echojournal.formatDuration
import com.example.echojournal.ui.screens.Components.getBackgroundColorForMood
import com.example.echojournal.ui.screens.Components.getPlayIconColorForMood
import com.example.echojournal.ui.theme.EchoJournalTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("UnrememberedMutableInteractionSource")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewEntryComponent(
    modifier: Modifier,
    audioData: ByteArray,
    timestamp: Long,
    duration: Long,
    onSaveComplete: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val sliderPosition = remember { mutableFloatStateOf(0f) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val database = remember { AudioRecordDatabase.getDatabase(context) }
    val audioRecordDao = remember { database.audioRecordDao() }
    var audioRecord by remember { mutableStateOf<AudioRecord?>(null) }
    var description by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var showHashtagSelector by remember { mutableStateOf(false) }
    var defaultTags by remember { mutableStateOf(listOf<String>()) }
    var isMoodSelected by remember { mutableStateOf(false) }
    var selectedMood by remember { mutableStateOf<String?>(null) } // To store selected Mood
    val isSaveEnabled by remember {
        derivedStateOf { defaultTags.isNotEmpty() && isMoodSelected }
    }

    Column(
        modifier = modifier.padding(start = 10.dp, end = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    showBottomSheet = true
                },
                modifier = Modifier.size(40.dp),

                ) {
                if (selectedMood != null) {
                    val moodIcon = getMoodIcon(selectedMood!!)
                    Image(
                        painter = painterResource(id = moodIcon),
                        contentDescription = selectedMood,
                        modifier = Modifier.size(40.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.add_mood),
                        contentDescription = selectedMood,
                        modifier = Modifier.size(40.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            context.getString(R.string.add_title),
                            color = colorResource(R.color.add_title_color),
                            style = TextStyle(fontSize = 27.sp)
                        )
                    },
                    textStyle = TextStyle(color = colorResource(R.color.black), fontSize = 27.sp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    )
                )
            }
        }

        if (selectedMood != null) {
            val backgroundColor = getBackgroundColorForMood(selectedMood!!)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(50))
                    .background(color = backgroundColor)
                    .padding(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White)
                        .size(40.dp)
                ) {
                    if (selectedMood != null) {
                        val playIconColor = getPlayIconColorForMood(selectedMood!!)
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = playIconColor
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))

                Slider(
                    value = sliderPosition.floatValue,
                    onValueChange = { sliderPosition.floatValue = it },
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    )
                )
                Spacer(modifier = Modifier.width(10.dp))
                val formattedDuration = formatDuration(duration)
                Log.d("Duration::::::::::", "Duration::::::$duration")
                Log.d("Duration::::::::::", "FormattedDuration::::::$formattedDuration")
                Text(text = "0:00/$formattedDuration")
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(50))
                    .background(color = colorResource(R.color.sad_bg))
                    .padding(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White)
                        .size(40.dp)
                ) {
                    Icon(
                        painter =  painterResource(id = R.drawable.audio_play),
                        contentDescription = "Play",
                        tint = colorResource(R.color.sad_play_icon)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))

                Slider(
                    value = sliderPosition.floatValue,
                    onValueChange = { sliderPosition.floatValue = it },
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    )
                )
                Spacer(modifier = Modifier.width(10.dp))
                val formattedDuration = formatDuration(duration)
                Log.d("Duration::::::::::", "Duration::::::$duration")
                Log.d("Duration::::::::::", "FormattedDuration::::::$formattedDuration")
                Text(text = "0:00/$formattedDuration")
            }
        }

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "#", fontSize = 18.sp,
                color = colorResource(R.color.add_title_color)
            )
            Spacer(Modifier.width(10.dp))

            Text(
                context.getString(R.string.topic),
                color = colorResource(R.color.add_title_color),
                style = TextStyle(fontSize = 18.sp),
                modifier = Modifier.clickable {
                    showHashtagSelector = true
                }
            )
        }

        // Show HashtagSelector when showHashtagSelector is true
        HashtagSelector(
            selectedTags = defaultTags,
            allTopics = Constants.ALL_TOPICS,
            onTagAdd = { tag ->
                if (!defaultTags.contains(tag)) {
                    defaultTags = defaultTags + tag
                }
                showHashtagSelector = false
            },
            onTagRemove = { tag ->
                defaultTags = defaultTags - tag
            },
            onDismiss = { showHashtagSelector = false },  // Add this
            expanded = showHashtagSelector,  // Pass the state directly
            modifier = Modifier.align(Alignment.Start)
        )

        ExpandableTextField(
            description = description,
            onDescriptionChange = { description = it }
        )

        Spacer(modifier.weight(1f))

        BottomBar(
            modifier = modifier,
            isConfirmVisible = true,
            isConfirmEnabled = isSaveEnabled,
            onConfirm = {
                saveAudioRecord(
                    title = title,
                    description = description,
                    selectedMood = selectedMood,
                    audioData = audioData,
                    timestamp = timestamp,
                    duration = duration,
                    audioRecord = audioRecord,
                    audioRecordDao = audioRecordDao,
                    defaultTags = defaultTags,
                    allTopics = Constants.ALL_TOPICS,
                    onSaveComplete = onSaveComplete
                )
            },
            onCancel = {
                onCancel()
                showBottomSheet = false
            },
        )
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = colorResource(R.color.white)
        ) {
            Mood(modifier = Modifier) { mood ->
                isMoodSelected = true
                isMoodSelected = mood != null
                selectedMood = mood
                showBottomSheet = false
            }
        }
    }
}

fun saveAudioRecord(
    title: String,
    description: String,
    selectedMood: String?,
    audioData: ByteArray,
    timestamp: Long,
    duration: Long,
    audioRecord: AudioRecord?,
    audioRecordDao: AudioRecordDao,
    defaultTags: List<String>,
    allTopics: List<String>,
    onSaveComplete: () -> Unit
) {
    val topicString = defaultTags.joinToString(",")
    val allTopicString = allTopics.joinToString("#")

    val newRecord = AudioRecord(
        title = title,
        description = description,
        mood = selectedMood ?: "",
        audioData = audioData,
        timestamp = timestamp,
        duration = duration,
        selectedTopic = topicString,
        allTopics = allTopicString
    )
    CoroutineScope(Dispatchers.IO).launch {
        audioRecordDao.insert(newRecord)
        withContext(Dispatchers.Main) {
            onSaveComplete()
        }
    }
}


fun getMoodIcon(selectedMood: String): Int {
    return when (selectedMood) {
        "Stressed" -> R.drawable.mood_stressed
        "Sad" -> R.drawable.mood_sad
        "Neutral" -> R.drawable.mood_neatral
        "Peaceful" -> R.drawable.mood_peaceful
        "Excited" -> R.drawable.mood_exited
        else -> R.drawable.mood // Default icon
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EchoJournalTheme {
    }
}