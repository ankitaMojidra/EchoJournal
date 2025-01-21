package com.example.echojournal.ui.screens.Components.NewRecordingComponents

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.echojournal.R
import com.example.echojournal.database.AudioRecord
import com.example.echojournal.database.AudioRecordDatabase
import com.example.echojournal.ui.theme.EchoJournalTheme


@SuppressLint("UnrememberedMutableInteractionSource")
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
    var topic by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedMoodForEntry by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var showHashtagSelector by remember { mutableStateOf(false) }
    var defaultTags by remember { mutableStateOf(listOf<String>()) }

    /*val defaultTags = remember {
        listOf(
            "Work",
            "Love",
            "Community",
            "Passion",
            "Desire",
            "Jane"
        ) } */

    var selectedTags by remember { mutableStateOf(listOf<String>()) }

    Column(
        modifier = modifier.padding(start = 10.dp, end = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    showBottomSheet = true
                },
                shape = CircleShape,
                modifier = Modifier.size(40.dp),
                contentPadding = PaddingValues(10.dp),
                colors = ButtonDefaults.buttonColors(
                    contentColor = colorResource(R.color.add_icon_plus),
                    containerColor = colorResource(R.color.cancel_backgroud)
                )
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
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
        Row(
            Modifier
                .fillMaxWidth()
                .background(
                    colorResource(R.color.confirm_color),
                    shape = RoundedCornerShape(50.dp)
                )
                .padding(8.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                },
                shape = CircleShape,
                modifier = Modifier.size(36.dp),
                contentPadding = PaddingValues(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(10.dp))

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
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "0:00/12:30")
        }

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "#", fontSize = 18.sp,
                color = colorResource(R.color.add_title_color)
            )
            Spacer(Modifier.width(10.dp))

            Text(
                context.getString(R.string.topic),
                color = colorResource(R.color.add_title_color),
                style = TextStyle(fontSize = 18.sp),
                modifier = Modifier.clickable {
                    showHashtagSelector = true }
            )

            /* TextField(
                 value = topic,
                 onValueChange = { topic = it },
                 modifier = Modifier.fillMaxWidth().clickable {
                     showHashtagSelector = true
                 },
                 placeholder = {
                     Text(
                         context.getString(R.string.topic),
                         color = colorResource(R.color.add_title_color),
                         style = TextStyle(fontSize = 18.sp),

                     )
                 },
                 textStyle = TextStyle(color = colorResource(R.color.black), fontSize = 18.sp),
                 colors = TextFieldDefaults.colors(
                     unfocusedContainerColor = Color.Transparent,
                     focusedContainerColor = Color.Transparent,
                     disabledContainerColor = Color.Transparent,
                     unfocusedIndicatorColor = Color.Transparent,
                     focusedIndicatorColor = Color.Transparent,
                     disabledIndicatorColor = Color.Transparent,
                 )
             )*/

        }

        // Show HashtagSelector when showHashtagSelector is true
        if (showHashtagSelector) {
            Dialog(
                onDismissRequest = { showHashtagSelector = false }
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    HashtagSelector(
                        selectedTags = defaultTags,
                        onTagAdd = { tag ->
                            if (!defaultTags.contains(tag)) {
                                defaultTags = defaultTags + tag
                            }
                        },
                        onTagRemove = { tag ->
                            defaultTags = defaultTags - tag
                        }
                    )
                }
            }
        }

        ExpandableTextField(
            description = description,
            onDescriptionChange = { description = it }
        )

        /* Row(
            Modifier
                .fillMaxWidth()
                .clickable {
                }, verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = "Edit",
                Modifier.size(20.dp),
                colorResource(R.color.add_title_color)
            )*/

        /* BasicTextField(
             value = description,
             onValueChange = { description = it },
             modifier = Modifier.fillMaxWidth(),
             textStyle = TextStyle(
                 color = colorResource(R.color.black),
                 fontSize = 18.sp
             ),
             maxLines = 3,
             decorationBox = @Composable { innerTextField ->
                 TextFieldDefaults.DecorationBox(
                     value = description,
                     innerTextField = {
                         Text(
                             text = description,
                             maxLines = 3,
                             overflow = TextOverflow.Ellipsis,
                             style = TextStyle(
                                 color = colorResource(R.color.black),
                                 fontSize = 18.sp,
                             )
                         )
                     },
                     enabled = true,
                     singleLine = false,
                     visualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
                     interactionSource = androidx.compose.foundation.interaction.MutableInteractionSource(),
                     placeholder = {
                         Text(
                             text = context.getString(R.string.add_description),
                             color = colorResource(R.color.add_title_color),
                             style = TextStyle(fontSize = 18.sp)
                         )
                     },
                     colors = TextFieldDefaults.colors(
                         unfocusedContainerColor = Color.Transparent,
                         focusedContainerColor = Color.Transparent,
                         disabledContainerColor = Color.Transparent,
                         unfocusedIndicatorColor = Color.Transparent,
                         focusedIndicatorColor = Color.Transparent,
                         disabledIndicatorColor = Color.Transparent,
                     ),
                 )
             },
         )
    }*/

        Spacer(modifier.weight(1f))
        BottomBar(modifier = modifier,
            isConfirmVisible = true,
            isConfirmEnabled = true,
            onConfirm = {
                // Update the audioRecord title before saving
                val updatedTitle = "selectedTags.joinToString"
                audioRecord?.let {
                    val updatedRecord = it.copy(title = updatedTitle)
                    // Assuming you have a function to update the record in the database
                    // For simplicity, I'm calling onSaveComplete directly, you'll need to handle the database update here
                    onSaveComplete()
                } ?: onSaveComplete()

            },
            onCancel = { showBottomSheet = false })
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = colorResource(R.color.white)
        ) {
            Mood(modifier = Modifier) {
                showBottomSheet = false
            }
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