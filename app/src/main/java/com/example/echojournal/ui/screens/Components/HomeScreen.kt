package com.example.echojournal.ui.screens.Components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
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
import com.example.echojournal.database.AudioRecordDatabase
import com.example.echojournal.ui.theme.EchoJournalTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
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
                val elapsedTimeMillis = currentTime - startTimeMillis.longValue - totalPausedTime.longValue

                recordingTime = String.format(
                    Locale.getDefault(),
                    "%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(elapsedTimeMillis),
                    TimeUnit.MILLISECONDS.toMinutes(elapsedTimeMillis) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(elapsedTimeMillis) % TimeUnit.MINUTES.toSeconds(1)
                )
                delay(100L)
            }
        }
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
        Text(text = context.getString(R.string.your_echo_general))

        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center)
        {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Image(painter = painterResource(R.drawable.icon), contentDescription = null)

                Text(text = context.getString(R.string.no_entries))
                Text(text = context.getString(R.string.start_recording))
            }

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
                    .padding(top = 15.dp)
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
                        }
                )

                if (!isRecordingVisible) {
                    Image(
                        painter = painterResource(R.drawable.icon_true_blue),
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                            .clickable {
                                stopRecording(mediaRecorder) {
                                    isRecording = it
                                    isPaused = false
                                    mediaRecorder = null
                                    startTimeMillis.longValue = 0L
                                    pausedTimeMillis.longValue = 0L
                                    totalPausedTime.longValue = 0L
                                    Toast.makeText(
                                        context,
                                        "Recording saved to ${outputFile.absolutePath}",
                                        Toast.LENGTH_LONG
                                    ).show()
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
                }else
                {
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
                                showBottomSheet = false
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
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        try {
            currentRecorder?.resume()
        } catch (e: IllegalStateException) {
            // If resume fails, start a new recording
            onRecorderCreated(startRecording(context, outputFile) { })
        }
    } else {
        // For API < 24, we need to start a new recording
        onRecorderCreated(startRecording(context, outputFile) { })
    }
}

private fun stopRecording(
    mediaRecorder: MediaRecorder?,
    onRecordingStateChange: (Boolean) -> Unit
) {
    mediaRecorder?.apply {
        try {
            stop()
            release()
            onRecordingStateChange(false)
        } catch (e: Exception) {
            onRecordingStateChange(false)
        }
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
    mediaRecorder.apply {
        setAudioSource(MediaRecorder.AudioSource.MIC)
        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        setOutputFile(outputFile.absolutePath)
        try {
            prepare()
            start()
            onRecordingStateChange(true)
            return this
        } catch (e: IOException) {
            Toast.makeText(context, "Error starting recording", Toast.LENGTH_SHORT).show()
            onRecordingStateChange(false)
            return null
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    EchoJournalTheme {
        HomeScreen(modifier = Modifier, navController = rememberNavController())
    }
}