package com.example.echojournal.ui.screens.Components

import android.widget.Space
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
import androidx.compose.runtime.getValue
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
import com.example.echojournal.ui.theme.EchoJournalTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier, navController: NavController) {

    val context = LocalContext.current
    val fabColor = Color(ContextCompat.getColor(context, R.color.add_icon))
    var showBottomSheet by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

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
                        showBottomSheet = true
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
                Text(text = "1:00:45", textAlign = TextAlign.Center)
            }

            Row(
                modifier = Modifier.padding(top = 15.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,

            ) {
                Image(
                    painter = painterResource(R.drawable.icon_close),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clickable {

                        }
                )

                Image(
                    painter = painterResource(R.drawable.icon_play),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clickable {

                        }
                )

                Image(
                    painter = painterResource(R.drawable.icon_true),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clickable {

                        }
                )
            }
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