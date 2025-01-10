package com.example.echojournal.ui.screens.Components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.echojournal.R
import com.example.echojournal.ui.theme.EchoJournalTheme

@Composable
fun Mood(modifier: Modifier, navController: NavController) {

    val context = LocalContext.current
    var isVisible by remember { mutableStateOf(false) }

    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = context.getString(R.string.how_are_you_doing))

        Spacer(modifier.height(10.dp))

        Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {

            Column(modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(R.drawable.stressed_gray),
                    contentDescription = "Stressed", modifier = Modifier
                        .size(50.dp),
                )

                Text(text = context.getString(R.string.stressed))
            }

            Column(modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(R.drawable.mood_sad),
                    contentDescription = "Sad",
                    modifier = Modifier
                        .size(50.dp)
                        .clickable {

                        })

                Text(text = context.getString(R.string.sad), textAlign = TextAlign.Center)
            }

            Column(modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Image(painter = painterResource(R.drawable.mood_neatral),
                    contentDescription = "Neutral", modifier = Modifier
                        .size(50.dp)
                        .clickable { })
                Text(text = context.getString(R.string.neatral))
            }

            Column(modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Image(painter = painterResource(R.drawable.mood_peaceful),
                    contentDescription = "Neutral", modifier = Modifier
                        .size(50.dp)
                        .clickable { })

                Text(text = context.getString(R.string.peaceful))
            }

            Column(modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {

                Image(painter = painterResource(R.drawable.mood_exited),
                    contentDescription = "Neutral", modifier = Modifier
                        .size(50.dp)
                        .clickable { })

                Text(text = context.getString(R.string.exited))
            }
        }

        Spacer(modifier.height(10.dp))

        Row(modifier.fillMaxWidth()) {
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

            Button(
                onClick = {},
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.confirm_color),
                    contentColor = colorResource(R.color.white)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 10.dp)

            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "confirm")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = context.getString(R.string.confirm))
            }

            if (isVisible) {
                Button(
                    onClick = {},
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.confirm_color),
                        contentColor = colorResource(R.color.white)
                    )
                ) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = context.getString(R.string.save))
                }
            }
        }
    }

}


@Preview
@Composable
fun MoodPreview() {
    EchoJournalTheme {
        Mood(modifier = Modifier, navController = rememberNavController())
    }
}