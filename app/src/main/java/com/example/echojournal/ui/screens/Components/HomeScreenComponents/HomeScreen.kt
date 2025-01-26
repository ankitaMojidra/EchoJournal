package com.example.echojournal.ui.screens.Components.HomeScreenComponents

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.echojournal.R
import com.example.echojournal.database.AudioRecordDao
import com.example.echojournal.database.AudioRecordDatabase
import com.example.echojournal.ui.screens.NewRecordingActivity
import com.example.echojournal.ui.theme.EchoJournalTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

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
    val audioRecords = audioRecordDao.getAllRecords().collectAsState(initial = emptyList())

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

        Log.d("HomeScreen", "Audio Record Size: ${audioRecords.value.size}")
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
        Text(
            text = context.getString(R.string.your_echo_general),
            modifier.padding(top = 30.dp, start = 10.dp)
        )

        if (audioRecords.value.isNotEmpty()) {
            Column {
                Row(modifier = Modifier.padding(start = 10.dp)) {
                    var showMoodDropDown by remember { mutableStateOf(false) }
                    var selectedMoods by remember { mutableStateOf(setOf<String>()) }
                    val selectedMoodText = selectedMoods.joinToString(", ")

                    var showTopicDropDown by remember { mutableStateOf(false) }
                    var selectedTopics by remember { mutableStateOf(setOf(String)) }
                    var selectedTopicText = selectedTopics.joinToString { ", " }

                    OutlinedButton(
                        onClick = {
                            showMoodDropDown = !showMoodDropDown
                        },
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, colorResource(R.color.add_title_color)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colorResource(R.color.all_mood)),
                    )
                    {
                        Text(selectedMoodText.ifEmpty { context.getString(R.string.all_moods) })
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                    if (showMoodDropDown) {
                        MoodDropDownMenu(
                            onDismiss = { showMoodDropDown = false },
                            selectedMoods = selectedMoods,
                            updateSelectedMoods = { newSelectedMoods ->
                                selectedMoods = newSelectedMoods
                            },
                        )
                    }
                    OutlinedButton(
                        onClick = {
                            showTopicDropDown = !showTopicDropDown
                        },
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, colorResource(R.color.add_title_color)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colorResource(R.color.all_mood))
                    ) {
                        Text(selectedTopicText.ifEmpty { context.getString(R.string.all_topics) })
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (showTopicDropDown) {
                        /*AllTopicDropDownMenu(
                            onDismiss = { showTopicDropDown = false },
                            selectedTopics = selectedTopics,
                            updateSelectedTopics = { newSelectedTopics ->
                                selectedTopics = newSelectedTopics
                            },
                        )*/
                    }
                }
                LazyColumn {
                    Log.d("HomeScreen", "Display Lazy column")

                    items(audioRecords.value) { record ->
                        RecordHistoryItem(record = record, onPlay = { })
                    }
                }
            }
        } else {
            Log.d("HomeScreen", "Display empty screen")

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(painter = painterResource(R.drawable.icon), contentDescription = null)
                Text(text = context.getString(R.string.no_entries))
                Text(text = context.getString(R.string.start_recording))
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
                Text(
                    text = context.getString(R.string.recording_your_memories),
                    textAlign = TextAlign.Center
                )
                Text(text = recordingTime, textAlign = TextAlign.Center)
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
                    val rippleColor1 = colorResource(R.color.ripple_first)
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
                        painter = painterResource(R.drawable.icon_pause),
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
        HomeScreen(modifier = Modifier, navController = rememberNavController())
    }
}