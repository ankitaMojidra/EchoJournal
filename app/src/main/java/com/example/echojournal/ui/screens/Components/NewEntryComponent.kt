package com.example.echojournal.ui.screens.Components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.echojournal.R
import com.example.echojournal.ui.theme.EchoJournalTheme

@Composable
fun NewEntryComponent(modifier: Modifier) {

    val context = LocalContext.current
    val sliderPosition = remember { mutableFloatStateOf(0f) }

    Column() {
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
                onValueChange = {},
                modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.titleSmall
            )
        }

        Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = {},
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
                onValueChange = {},
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
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = colorResource(R.color.add_title_color))
            )
        }
    }
}


@Preview
@Composable
fun NewEntryComponentPreview() {
    EchoJournalTheme {
        NewEntryComponent(modifier = Modifier)
    }
}