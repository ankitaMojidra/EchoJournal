package com.example.echojournal.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.compose.rememberNavController
import com.example.echojournal.R
import com.example.echojournal.ui.screens.Components.NewRecordingComponents.NewEntryComponent
import com.example.echojournal.ui.theme.EchoJournalTheme

class NewRecordingActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val audioRecordId = intent.getIntExtra("audioRecordId", 0)

        setContent {
            EchoJournalTheme {
                val navCompiler = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CenterAlignedTopAppBar(title = {
                            Text(
                                text = "New Entry",
                                textAlign = TextAlign.Center,
                                color = colorResource(R.color.black)
                            )
                        },
                            navigationIcon = {
                                IconButton(onClick = { navCompiler.popBackStack() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = null,
                                        tint = colorResource(R.color.black)
                                    )
                                }
                            }
                        )
                    },
                )
                { innerPadding ->
                    NewEntryComponent(modifier = Modifier.padding(innerPadding),
                        audioRecordId = audioRecordId,
                        onSaveComplete = { finish() },
                        onCancel = { finish() }
                    )
                }
            }
        }
    }
}

