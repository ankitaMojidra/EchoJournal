package com.example.echojournal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.echojournal.ui.screens.Components.HomeScreen
import com.example.echojournal.ui.theme.EchoJournalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            EchoJournalTheme {

                val navCompiler = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                   /* Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )*/
                    HomeScreen(modifier = Modifier.padding(innerPadding), navCompiler)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EchoJournalTheme {
        Greeting("Android")
    }
}