import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import com.example.echojournal.ui.screens.components.getBackgroundColorForMood
import com.example.echojournal.ui.screens.components.getPlayIconColorForMood
import com.example.echojournal.ui.screens.components.newrecordingcomponents.BottomBar
import com.example.echojournal.ui.screens.components.newrecordingcomponents.ExpandableTextField
import com.example.echojournal.ui.screens.components.newrecordingcomponents.Mood
import com.example.echojournal.ui.screens.components.newrecordingcomponents.TagChip
import com.example.echojournal.ui.theme.EchoJournalTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@SuppressLint("UnrememberedMutableInteractionSource")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NewEntryComponent(
    modifier: Modifier,
    audioData: ByteArray,
    timestamp: Long,
    duration: Long,
    onSaveComplete: () -> Unit,
    onCancel: () -> Unit,
    onPlay: () -> Unit
) {
    val context = LocalContext.current
    val sliderPosition = remember { mutableFloatStateOf(0f) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val database = remember { AudioRecordDatabase.getDatabase(context) }
    val audioRecordDao = remember { database.audioRecordDao() }
    val audioRecord by remember { mutableStateOf<AudioRecord?>(null) }
    var description by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var defaultTags by remember { mutableStateOf(listOf<String>()) }
    var isMoodSelected by remember { mutableStateOf(false) }
    var selectedMood by remember { mutableStateOf<String?>(null) }
    var currentPosition by remember { mutableIntStateOf(0) }
    val mediaPlayer = remember { MediaPlayer() }
    var isPlaying by remember { mutableStateOf(false) }

    // New state for hashtag selector
    var searchText by remember { mutableStateOf("") }
    var isHashtagExpanded by remember { mutableStateOf(false) }

    val isSaveEnabled by remember {
        derivedStateOf { defaultTags.isNotEmpty() && isMoodSelected }
    }

    // Function to update the current playback time
    fun updatePlaybackTime() {
        if (mediaPlayer.isPlaying) {
            currentPosition = mediaPlayer.currentPosition
            val progress = currentPosition.toFloat() / duration.toFloat()
            sliderPosition.floatValue = progress.coerceIn(0f, 1f)
        }
    }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            updatePlaybackTime()
            delay(200)
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

    Column(
        modifier = modifier
            .padding(start = 10.dp, end = 10.dp)
            .background(color = Color.White),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Mood and Title section
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { showBottomSheet = true },
                modifier = Modifier.size(40.dp),
            ) {
                if (selectedMood != null) {
                    Image(
                        painter = painterResource(id = getMoodIcon(selectedMood!!)),
                        contentDescription = selectedMood,
                        modifier = Modifier.size(40.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.add_mood),
                        contentDescription = null,
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

        // Audio Player section
        if (selectedMood != null) {
            val backgroundColor = getBackgroundColorForMood(selectedMood!!)
            val playIconColor = getPlayIconColorForMood(selectedMood!!)

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
                            playAudio(audioData)
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
                        .height(30.dp), // Adjust height here
                    colors = SliderDefaults.colors(
                        thumbColor = Color.Transparent,
                        activeTrackColor = getPlayIconColorForMood(selectedMood!!),
                        inactiveTrackColor = getPlayIconColorForMood(selectedMood!!).copy(alpha = 0.2f)
                    ),
                    thumb = {
                        SliderDefaults.Thumb(
                            interactionSource = remember { MutableInteractionSource() },
                            colors = SliderDefaults.colors(thumbColor = Color.Transparent),
                            modifier = Modifier.size(0.dp)
                        )
                    },
                )

                Spacer(modifier = Modifier.width(10.dp))
                val formattedDuration = formatDuration(duration)
                Text(
                    text = "${formatDuration(currentPosition.toLong())}/$formattedDuration",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorResource(R.color.audio_time),
                    modifier = Modifier.padding(end = 8.dp)
                )
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
                    onClick = {
                        if (!isPlaying) {
                            playAudio(audioData)
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
                    if (isPlaying) {
                        Icon(
                            imageVector = Icons.Filled.Pause,
                            contentDescription = "Play",
                            tint = colorResource(R.color.sad_play_icon)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = colorResource(R.color.sad_play_icon)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))

                Slider(
                    value = sliderPosition.floatValue,
                    onValueChange = { sliderPosition.floatValue = it },
                    modifier = Modifier
                        .weight(1f)
                        .height(18.dp), // Adjust height here
                    colors = SliderDefaults.colors(
                        thumbColor = Color.Transparent,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    ),
                    thumb = {
                        SliderDefaults.Thumb(
                            interactionSource = remember { MutableInteractionSource() },
                            colors = SliderDefaults.colors(thumbColor = Color.Transparent),
                            modifier = Modifier.size(0.dp)
                        )
                    },
                    track = {
                        SliderDefaults.Track(
                            sliderState = it,
                            modifier = Modifier
                                .height(25.dp),
                            colors = SliderDefaults.colors(
                                activeTrackColor = colorResource(R.color.sad_play_icon),
                                inactiveTrackColor = colorResource(R.color.sad_play_icon).copy(alpha = 0.2f)
                            )
                        )
                    },
                )


                Spacer(modifier = Modifier.width(10.dp))
                val formattedDuration = formatDuration(duration)
                Text(
                    text = "${formatDuration(currentPosition.toLong())}/$formattedDuration",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorResource(R.color.audio_time),
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }


        // Hashtag selector section
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#",
                fontSize = 20.sp,
                color = colorResource(R.color.add_title_color)
            )
            Spacer(Modifier.width(18.dp))

            Box(
                modifier = Modifier
                    .weight(1f) // This ensures the Box takes remaining width
            ) {
            ExposedDropdownMenuBox(
                expanded = isHashtagExpanded,
                onExpandedChange = { isHashtagExpanded = it }
            ) {
                BasicTextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                        isHashtagExpanded = true
                    },
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        color = colorResource(R.color.add_title_color)
                    ),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        Box {
                            if (searchText.isEmpty()) {
                                Text(
                                    context.getString(R.string.topic),
                                    color = colorResource(R.color.add_title_color),
                                    fontSize = 18.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                // Dropdown menu for tags
                val filteredTags = remember(searchText) {
                    Constants.ALL_TOPICS.filter {
                        it.lowercase().contains(searchText.lowercase())
                    }
                }

                ExposedDropdownMenu(
                    expanded = isHashtagExpanded && (searchText.isNotEmpty() || filteredTags.isNotEmpty()),
                    onDismissRequest = {
                        isHashtagExpanded = false
                        searchText = ""
                    },
                    modifier = Modifier
                        .exposedDropdownSize(matchTextFieldWidth = true)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    filteredTags.forEach { tag ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("#")
                                    Text(tag)
                                }
                            },
                            onClick = {
                                if (!defaultTags.contains(tag)) {
                                    defaultTags = defaultTags + tag
                                }
                                searchText = ""
                                isHashtagExpanded = false
                            }
                        )
                    }

                    if (searchText.isNotEmpty() && !filteredTags.any {
                            it.equals(searchText, ignoreCase = true)
                        }) {
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("+ Create '")
                                    Text("#$searchText")
                                    Text("'")
                                }
                            },
                            onClick = {
                                if (!defaultTags.contains(searchText)) {
                                    defaultTags = defaultTags + searchText
                                }
                                searchText = ""
                                isHashtagExpanded = false
                            }
                        )
                    }
                }
            }
        }
        }

        // Selected tags display
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            defaultTags.forEach { tag ->
                TagChip(
                    text = tag,
                    onRemove = {
                        defaultTags = defaultTags - tag
                    },
                    modifier = Modifier.padding(end = 4.dp, bottom = 4.dp)
                )
            }
        }

        // Description field
        ExpandableTextField(
            description = description,
            onDescriptionChange = { description = it }
        )

        Spacer(modifier.weight(1f))

        // Bottom bar
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

    // Mood bottom sheet
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