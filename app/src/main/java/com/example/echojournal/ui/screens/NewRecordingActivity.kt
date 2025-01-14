package com.example.echojournal.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.echojournal.R
import com.example.echojournal.database.AudioRecord
import com.example.echojournal.database.AudioRecordDatabase
import com.example.echojournal.ui.screens.Components.Mood
import com.example.echojournal.ui.theme.EchoJournalTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewRecordingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val audioRecordId = intent.getIntExtra("audioRecordId", 0)

        setContent {
            EchoJournalTheme {
                val navCompiler = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NewEntryComponent(modifier = Modifier.padding(innerPadding),
                        audioRecordId = audioRecordId,
                        onSaveComplete = { finish() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewEntryComponent(modifier: Modifier, audioRecordId: Int, onSaveComplete: () -> Unit) {

    val context = LocalContext.current
    val sliderPosition = remember { mutableFloatStateOf(0f) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val database = remember { AudioRecordDatabase.getDatabase(context) }
    val audioRecordDao = remember { database.audioRecordDao() }
    var audioRecord by remember { mutableStateOf<AudioRecord?>(null) }
    var title by remember { mutableStateOf("") }
    var topic by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var mood by remember { mutableStateOf("") }

    LaunchedEffect(audioRecordId) {
        audioRecordDao.getRecordById(audioRecordId).collect { record ->
            record?.let {
                audioRecord = it
                title = it.title
                topic = it.topic
                description = it.description
                mood = it.mood
            }
        }
    }

    Column {
        Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = {},
                shape = CircleShape,
                modifier = Modifier.size(36.dp),
                contentPadding = PaddingValues(10.dp),
                colors = ButtonDefaults.buttonColors(
                    contentColor = colorResource(R.color.add_icon_plus),
                    containerColor = colorResource(R.color.cancel_backgroud)
                )
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
            Spacer(modifier.width(8.dp))

            TextField(
                value = context.getString(R.string.add_title),
                onValueChange = { title = it },
                modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.titleSmall
            )
        }

        Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = {
                    showBottomSheet = true
                },
                shape = CircleShape,
                modifier = Modifier.size(36.dp),
                contentPadding = PaddingValues(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White
                )
            }
            Spacer(modifier.width(10.dp))

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

            Spacer(modifier.width(10.dp))
            Text(text = "0:00/12:30")
        }

        Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "#")
            Spacer(Modifier.width(10.dp))
            TextField(
                value = context.getString(R.string.topic),
                onValueChange = {

                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = colorResource(R.color.add_title_color))
            )
            Spacer(modifier.height(10.dp))
        }

        Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit")
            Spacer(Modifier.width(10.dp))
            TextField(
                value = context.getString(R.string.add_description),
                onValueChange = {
                    topic = it
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = colorResource(R.color.add_title_color))
            )
        }

        Button(
            onClick = {},
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.cancel_backgroud),
                contentColor = colorResource(R.color.cancel_color)
            ),
            modifier = Modifier
                .wrapContentWidth()
                .padding(start = 10.dp)
        ) {
            Text(text = context.getString(R.string.cancel))
        }

        Spacer(modifier.width(10.dp))

        val isSaveEnabled = title.isNotBlank() && topic.isNotBlank()

        Button(
            onClick = {
                audioRecord?.let { recordToUpdate ->
                    val updatedRecord = recordToUpdate.copy(
                        topic = topic,
                        description = description,
                        mood = mood
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        audioRecordDao.update(updatedRecord)
                        onSaveComplete() // Call the callback to finish the activity
                    }
                }
            },
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSaveEnabled) colorResource(R.color.confirm_color) else colorResource(
                    R.color.save_color
                ),
                contentColor = colorResource(R.color.white)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 10.dp)
        ) {
            Icon(imageVector = Icons.Default.Check, contentDescription = "confirm")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = context.getString(R.string.save))
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = colorResource(R.color.white)
        ) {
            Mood(modifier = Modifier)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EchoJournalTheme {
        NewEntryComponent(modifier = Modifier, audioRecordId = 1, onSaveComplete = {})
    }
}