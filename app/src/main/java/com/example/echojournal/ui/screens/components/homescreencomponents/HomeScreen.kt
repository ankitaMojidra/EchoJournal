package com.example.echojournal.ui.screens.components.homescreencomponents

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import com.example.echojournal.R
import com.example.echojournal.database.AudioRecord
import com.example.echojournal.database.AudioRecordDao
import com.example.echojournal.database.AudioRecordDatabase
import com.example.echojournal.getRelativeDay
import com.example.echojournal.ui.screens.NewRecordingActivity
import com.example.echojournal.ui.theme.EchoJournalTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier, navController: NavController) {

    val context = LocalContext.current

    val fabColor = Color(ContextCompat.getColor(context, R.color.add_icon))
    var showBottomSheet by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    var isRecording by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var mediaRecorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var recordingTime by remember { mutableStateOf("00:00:00") }
    val startTimeMillis = remember { mutableLongStateOf(0L) }
    var isRecordingVisible by remember { mutableStateOf(false) }

    val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    val outputFileName = "recording_${sdf.format(Date())}.mp3"
    val outputFile = File(context.cacheDir, outputFileName)
    val pausedTimeMillis = remember { mutableLongStateOf(0L) }
    val totalPausedTime = remember { mutableLongStateOf(0L) }

    val database = remember { AudioRecordDatabase.getDatabase(context) }
    val audioRecordDao = remember { database.audioRecordDao() }
    val allAudioRecords by audioRecordDao.getAllRecords()
        .collectAsState(initial = null) // Changed here for nullability

    // Selected moods for filtering
    var selectedMoods by remember { mutableStateOf(setOf<String>()) }
    var selectedTopicsWithDB by remember { mutableStateOf<Set<String>>(emptySet()) }  // Fix the type declaration

    // Filtered list of audio records
    val filteredAudioRecords by remember(
        allAudioRecords,
        selectedMoods,
        selectedTopicsWithDB
    ) {
        Log.d(
            "HomeScreen",
            "Remember Block for filtering : selectedMoods = $selectedMoods , selectedTopicsWithDB = $selectedTopicsWithDB"
        )
        mutableStateOf(
            allAudioRecords?.let {
                filterAudioRecordsByMoodAndTopic(
                    it,
                    selectedMoods,
                    selectedTopicsWithDB
                )
            }
                ?: emptyList()
        )
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                // **Corrected update in permission callback**
                startRecording(context, outputFile) { isRecording = it }.let { mediaRecorder = it }
            } else {
                Toast.makeText(context, "Audio recording permission denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    )

    LaunchedEffect(isRecording, isPaused) {
        if (isRecording && !isPaused) {
            if (startTimeMillis.longValue == 0L) {
                startTimeMillis.longValue = System.currentTimeMillis()
            } else if (pausedTimeMillis.longValue > 0L) {
                // Add the paused duration to total paused time
                totalPausedTime.value += System.currentTimeMillis() - pausedTimeMillis.longValue
                pausedTimeMillis.longValue = 0L
            }

            while (isRecording && !isPaused) {
                val currentTime = System.currentTimeMillis()
                val elapsedTimeMillis =
                    currentTime - startTimeMillis.longValue - totalPausedTime.longValue

                recordingTime = String.format(
                    Locale.getDefault(),
                    "%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(elapsedTimeMillis),
                    TimeUnit.MILLISECONDS.toMinutes(elapsedTimeMillis) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(elapsedTimeMillis) % TimeUnit.MINUTES.toSeconds(
                        1
                    )
                )
                delay(100L)
            }
        }

        Log.d("HomeScreen", "Audio Record Size: ${allAudioRecords}")
    }

    fun startRecordingEcho() {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startRecording(context, outputFile) { isRecording = it }.let { mediaRecorder = it }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    Column {
        if (allAudioRecords != null) {
            Text(
                text = context.getString(R.string.your_echo_general),
                modifier.padding(top = 30.dp, start = 10.dp),
                color = colorResource(R.color.your_echo_general),
                fontSize = 25.sp,
                fontWeight = FontWeight.SemiBold
            )
            if (allAudioRecords!!.isNotEmpty()) {
                Column {
                    Row(modifier = Modifier.padding(start = 10.dp)) {
                        var showMoodDropDown by remember { mutableStateOf(false) } //  Added
                        val selectedMoodText = selectedMoods.joinToString(", ")
                        val selectedTopicText = selectedTopicsWithDB.joinToString(", ")
                        val moodItems = getMoodItems(context)  // Get mood items
                        var showTopicDropDown by remember { mutableStateOf(false) }

                        OutlinedButton(
                            onClick = {
                                showMoodDropDown = !showMoodDropDown
                            },
                            shape = RoundedCornerShape(20.dp),
                            border = if (selectedMoodText.isEmpty()) {
                                BorderStroke(1.dp, colorResource(R.color.add_title_color))
                            } else {
                                BorderStroke(1.dp, colorResource(R.color.selected_border_color))
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = colorResource(R.color.all_mood),
                                containerColor = if (selectedMoodText.isEmpty()) colorResource(R.color.default_color) else Color.White
                            )
                        )
                        {
                            if (selectedMoodText.isEmpty()) {
                                Text(context.getString(R.string.all_moods))
                            } else {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Display all emojis first
                                    Row(
                                        horizontalArrangement = Arrangement.Start,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        selectedMoods.forEach { selectedMood ->
                                            val moodItem =
                                                moodItems.find { it.name == selectedMood }
                                            moodItem?.let { mood ->
                                                mood.icon?.let { icon ->
                                                    Image(
                                                        bitmap = icon.toBitmap().asImageBitmap(),
                                                        contentDescription = mood.name,
                                                        modifier = Modifier
                                                            .size(20.dp)
                                                            .padding(end = 4.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(4.dp))

                                    // Display all texts after emojis
                                    Row(
                                        horizontalArrangement = Arrangement.Start,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        selectedMoods.forEachIndexed { index, selectedMood ->
                                            Text(
                                                text = selectedMood + if (index < selectedMoods.size - 1) ", " else ""
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    // Display for close icon
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Reset Filter",
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clickable { selectedMoods = emptySet() }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        if (showMoodDropDown) {
                            MoodDropDownMenu(
                                onDismiss = {
                                    showMoodDropDown = false
                                },
                                selectedMoods = selectedMoods,
                                updateSelectedMoods = { newSelectedMoods ->
                                    Log.d(
                                        "HomeScreen",
                                        "Mood Drop Down Update selected moods $newSelectedMoods"
                                    )
                                    selectedMoods = newSelectedMoods
                                },
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                showTopicDropDown = !showTopicDropDown
                            },
                            shape = RoundedCornerShape(20.dp),
                            border = if (selectedTopicText.isEmpty()) {
                                BorderStroke(1.dp, colorResource(R.color.add_title_color))
                            } else {
                                BorderStroke(1.dp, colorResource(R.color.selected_border_color))
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = colorResource(R.color.all_mood),
                                containerColor = if (selectedTopicText.isEmpty()) colorResource(R.color.default_color) else Color.White
                            )
                        ) {
                            if (selectedTopicText.isEmpty()) {
                                Text(context.getString(R.string.all_topics))
                            } else {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                )
                                {
                                    if (selectedTopicsWithDB.size > 2) {
                                        Text(text = "+1")
                                    } else {
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (selectedTopicsWithDB.size <= 2) {
                                                Row(
                                                    horizontalArrangement = Arrangement.Start,
                                                    verticalAlignment = Alignment.CenterVertically
                                                )
                                                {
                                                    selectedTopicsWithDB.forEachIndexed { index, selectedTopic ->
                                                        Text(
                                                            text = selectedTopic + if (index < selectedTopicsWithDB.size - 1) ", " else ""
                                                        )
                                                    }
                                                }
                                            } else {
                                                Row(
                                                    horizontalArrangement = Arrangement.Start,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    selectedTopicsWithDB.take(2)
                                                        .forEachIndexed { index, selectedTopic ->
                                                            Text(
                                                                text = selectedTopic + if (index < 1) ", " else ""
                                                            )
                                                        }
                                                    Text(text = "+${selectedTopicsWithDB.size - 2}")
                                                }
                                            }
                                        }
                                    }
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Reset Filter",
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clickable { selectedTopicsWithDB = emptySet() }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        var allTopics by remember { mutableStateOf(listOf<String>()) }

                        // Fetch topics when component loads
                        LaunchedEffect(Unit) {
                            val topics = withContext(Dispatchers.IO) {
                                // Get topics and split by "#" since they're stored as "#topic1#topic2#topic3"
                                audioRecordDao.getAllTopics()
                                    .firstOrNull()
                                    ?.split("#")
                                    ?.filter { it.isNotEmpty() }
                                    ?: emptyList()
                            }
                            allTopics = topics
                        }

                        if (showTopicDropDown) {
                            AllTopicDropDownMenu(
                                onDismiss = { showTopicDropDown = false },
                                selectedTopics = selectedTopicsWithDB,
                                updateSelectedTopics = { newSelectedTopics ->
                                    selectedTopicsWithDB = newSelectedTopics
                                },
                                topics = allTopics
                            )
                        }
                    }
                    LazyColumn {
                        Log.d("HomeScreen", "Display Lazy column")

                        itemsIndexed(
                            items = filteredAudioRecords,
                            key = { _, record -> record.id }
                        ) { index, record ->
                            val nextRecord =
                                if (index < filteredAudioRecords.size - 1) filteredAudioRecords[index + 1] else null
                            val showSeparatorLine = nextRecord?.let {
                                getRelativeDay(record.timestamp) == getRelativeDay(it.timestamp)
                            } ?: false
                            val isFirstRecordOfDay = index == 0 ||
                                    getRelativeDay(filteredAudioRecords[index - 1].timestamp) != getRelativeDay(
                                record.timestamp
                            )

                            RecordHistoryItem(
                                record = record,
                                showSeparatorLine = showSeparatorLine,
                                isFirstRecordOfDay = isFirstRecordOfDay,
                                filteredAudioRecords = filteredAudioRecords,
                                index = index,
                                onPlay = { }
                            )
                        }
                    }
                }
            } else {
                Log.d("HomeScreen", "Display empty screen")
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(painter = painterResource(R.drawable.icon), contentDescription = null)
                    Text(text = context.getString(R.string.no_entries))
                    Text(text = context.getString(R.string.start_recording))
                }
            }
        }
    }

    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center)
    {
        FloatingActionButton(
            onClick = {
                coroutineScope.launch {
                    startRecordingEcho() // Start recording when FAB is clicked
                    showBottomSheet = true
                    isRecordingVisible = false
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(10.dp),
            shape = CircleShape,
            containerColor = fabColor,
            contentColor = Color.White
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Echo")
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = colorResource(R.color.white)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!isRecordingVisible) {
                    Text(
                        text = context.getString(R.string.recording_your_memories),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Text(
                        text = context.getString(R.string.recording_paused),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(text = recordingTime, textAlign = TextAlign.Center, fontSize = 12.sp)
            }

            Row(
                modifier = Modifier
                    .padding(top = 45.dp, bottom = 30.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.icon_close),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clickable {
                            showBottomSheet = false
                            isRecording = false
                            isPaused = false
                            recordingTime = "00:00:00"
                            startTimeMillis.longValue = 0L
                            pausedTimeMillis.longValue = 0L
                            totalPausedTime.longValue = 0L
                            mediaRecorder?.stop()
                            mediaRecorder?.release()
                            mediaRecorder = null
                        }
                )

                if (!isRecordingVisible) {
                    val rippleColor1 = colorResource(R.color.sad_bg)
                    val rippleColor2 = colorResource(R.color.ripple_second)
                    val rippleRadius1 = remember { Animatable(0f) }
                    val rippleRadius2 = remember { Animatable(0f) }

                    LaunchedEffect(showBottomSheet, isRecording) {
                        if (showBottomSheet && isRecording) {
                            while (true) {
                                launch {
                                    rippleRadius1.animateTo(
                                        targetValue = 50f,
                                        animationSpec = tween(durationMillis = 600)
                                    )
                                    rippleRadius1.snapTo(0f)
                                }
                                launch {
                                    delay(200) // Offset for the second ripple
                                    rippleRadius2.animateTo(
                                        targetValue = 40f,
                                        animationSpec = tween(durationMillis = 600)
                                    )
                                    rippleRadius2.snapTo(0f)
                                }
                                delay(600)
                            }
                        } else {
                            rippleRadius1.snapTo(0f)
                            rippleRadius2.snapTo(0f)
                        }
                    }

                    Image(
                        painter = painterResource(R.drawable.icon_true_blue),
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                            .drawBehind {
                                if (isRecording) {
                                    drawCircle(
                                        color = rippleColor1,
                                        radius = rippleRadius1.value.dp.toPx(),
                                        center = center,
                                    )
                                    drawCircle(
                                        color = rippleColor2,
                                        radius = rippleRadius2.value.dp.toPx(),
                                        center = center,
                                    )
                                }
                            }
                            .clickable {
                                stopRecordingAndSave(
                                    context,
                                    mediaRecorder,
                                    audioRecordDao
                                ) { success ->
                                    isRecording = success
                                    isPaused = false
                                    mediaRecorder = null
                                    startTimeMillis.longValue = 0L
                                    pausedTimeMillis.longValue = 0L
                                    totalPausedTime.longValue = 0L
                                    showBottomSheet = false
                                }
                            }
                    )

                    Image(
                        painter = painterResource(R.drawable.icon_pause_blue),
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                            .clickable {
                                isPaused = true
                                pausedTimeMillis.longValue = System.currentTimeMillis()
                                pauseRecording(mediaRecorder)
                                isRecordingVisible = true
                            }
                    )
                } else {
                    Image(painter = painterResource(R.drawable.icon_record),
                        contentDescription = "audio_start",
                        modifier = Modifier
                            .size(50.dp)
                            .clickable {
                                isPaused = false
                                resumeRecording(
                                    context,
                                    outputFile,
                                    mediaRecorder
                                ) { recorder ->
                                    mediaRecorder = recorder
                                }
                                isRecordingVisible = false
                            }
                    )

                    Image(painter = painterResource(R.drawable.icon_true),
                        contentDescription = "Audio save & Stop",
                        modifier = Modifier
                            .size(50.dp)
                            .clickable {
                                stopRecordingAndSave(context, mediaRecorder, audioRecordDao) {
                                    isRecording = it
                                    isPaused = false
                                    mediaRecorder = null
                                    startTimeMillis.longValue = 0L
                                    pausedTimeMillis.longValue = 0L
                                    totalPausedTime.longValue = 0L
                                    showBottomSheet = false
                                }
                            })
                }
            }
        }
    }
}

private fun filterAudioRecordsByMoodAndTopic(
    records: List<AudioRecord>,
    selectedMoods: Set<String>,
    selectedTopics: Set<String>
): List<AudioRecord> {
    Log.d(
        "HomeScreen",
        "filterAudioRecordsByMoodAndTopic: selectedMoods = $selectedMoods, selectedTopics = $selectedTopics"
    )

    if (selectedMoods.isEmpty() && selectedTopics.isEmpty()) {
        Log.d(
            "HomeScreen",
            "filterAudioRecordsByMoodAndTopic: no moods or topics selected, returning all records"
        )
        return records // Return all records if no moods are selected
    }
    val filteredList = records.filter { record ->
        val moodMatch = selectedMoods.isEmpty() || selectedMoods.any { it == record.mood }
        val topicMatch =
            selectedTopics.isEmpty() || selectedTopics.any { it == record.selectedTopic }

        val isMatch = moodMatch && topicMatch

        Log.d(
            "HomeScreen",
            "filterAudioRecordsByMoodAndTopic: record mood = ${record.mood}, match = $isMatch, selected mood ${selectedMoods}, selected topic ${selectedTopics} record topic ${record.selectedTopic}"
        )
        isMatch
    }

    Log.d(
        "HomeScreen",
        "filterAudioRecordsByMoodAndTopic: filtered list size = ${filteredList.size}"
    )
    return filteredList
}

private fun pauseRecording(mediaRecorder: MediaRecorder?) {
    mediaRecorder?.pause()
}

private fun resumeRecording(
    context: Context,
    outputFile: File,
    currentRecorder: MediaRecorder?,
    onRecorderCreated: (MediaRecorder?) -> Unit
) {
    try {
        currentRecorder?.resume()
    } catch (e: IllegalStateException) {
        // If resume fails, start a new recording
        onRecorderCreated(startRecording(context, outputFile) { })
    }
}

private fun stopRecordingAndSave(
    context: Context,
    mediaRecorder: MediaRecorder?,
    audioRecordDao: AudioRecordDao,
    onRecordingStateChange: (Boolean) -> Unit
) {
    mediaRecorder?.apply {
        try {
            stop()
            val recordingFile = File(context.cacheDir, "temp_recording.mp3")
            setOutputFile(recordingFile.absolutePath) // Set output to a temp file

            // Get audio duration
            val duration = getAudioDuration(recordingFile.absolutePath)

            // Need to reconfigure the MediaRecorder to extract data. It's better to store to a temp file then read.
            val inputStream = FileInputStream(recordingFile)
            val audioBytes = inputStream.readBytes()
            inputStream.close()
            recordingFile.delete() // Delete the temp file
            val timestamp = System.currentTimeMillis()

            release()
            onRecordingStateChange(false)
            val intent = Intent(context, NewRecordingActivity::class.java).apply {
                putExtra("audioData", audioBytes)
                putExtra("timestamp", timestamp)
                putExtra("duration", duration)

                //Pass selected mood to New Recording Activity
            }
            context.startActivity(intent)

        } catch (e: Exception) {
            Toast.makeText(context, "Error saving recording", Toast.LENGTH_SHORT).show()
            onRecordingStateChange(false)
        }
    }
}

private fun getAudioDuration(filePath: String): Long {
    val retriever = MediaMetadataRetriever()
    try {
        retriever.setDataSource(filePath)
        val durationString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        return durationString?.toLongOrNull() ?: 0L
    } catch (e: Exception) {
        e.printStackTrace()
        return 0L
    } finally {
        retriever.release()
    }
}

private fun startRecording(
    context: Context,
    outputFile: File,
    onRecordingStateChange: (Boolean) -> Unit
): MediaRecorder? {
    val mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        MediaRecorder(context)
    } else {
        @Suppress("DEPRECATION")
        MediaRecorder()
    }
    try {
        mediaRecorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(
                File(
                    context.cacheDir,
                    "temp_recording.mp3"
                ).absolutePath
            ) // Temporary file

            prepare()
            start()
            onRecordingStateChange(true)
            return this
        }
    } catch (e: IOException) {
        Toast.makeText(context, "Error starting recording", Toast.LENGTH_SHORT).show()
        onRecordingStateChange(false)
        return null
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    EchoJournalTheme {
        // HomeScreen(modifier = Modifier, navController = rememberNavController())
    }
}