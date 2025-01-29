package com.example.echojournal.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.echojournal.R
import com.example.echojournal.ui.screens.Components.NewRecordingComponents.NewEntryComponent
import com.example.echojournal.ui.theme.EchoJournalTheme

@OptIn(ExperimentalMaterial3Api::class)
class NewRecordingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val audioData = intent.getByteArrayExtra("audioData")
        val timestamp = intent.getLongExtra("timestamp", 0L)
        val duration = intent.getLongExtra("duration", 0L)

        setContent {
            EchoJournalTheme {
                val navCompiler = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize().padding(top = 50.dp),
                    topBar = {
                        CenterAlignedTopAppBar(title = {
                            Text(
                                text = "New Entry",
                                textAlign = TextAlign.Center,
                                color = colorResource(R.color.all_mood),
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                            navigationIcon = {
                                IconButton(onClick = { navCompiler.popBackStack()
                                finish()}) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = null,
                                        tint = colorResource(R.color.all_mood)
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = Color.White
                            )
                        )
                    },
                )
                { innerPadding ->
                    NewEntryComponent(modifier = Modifier.padding(innerPadding),
                        audioData = audioData ?: byteArrayOf() ,
                        timestamp = timestamp,
                        duration = duration,
                        onSaveComplete = { finish() },
                        onCancel = { finish() }
                    )
                }
            }
        }
    }
}

