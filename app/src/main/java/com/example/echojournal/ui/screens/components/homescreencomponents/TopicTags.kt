package com.example.echojournal.ui.screens.components.homescreencomponents

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.echojournal.R

@Composable
fun TopicTags(topicString: String) {
    val topics = topicString.split(",")
    Row {
        topics.forEach { topic ->
            Surface(
                modifier = Modifier.padding(end = 10.dp),
                shape = RoundedCornerShape(16.dp),
                color = colorResource(R.color.topic_tag_bg)
            ) {
                Text(
                    text = "# ${topic.trim()}",
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 5.dp),
                    fontSize = 13.sp
                )
            }
        }
    }
}
