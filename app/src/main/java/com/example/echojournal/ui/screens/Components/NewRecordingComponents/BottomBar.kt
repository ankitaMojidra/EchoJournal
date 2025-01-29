package com.example.echojournal.ui.screens.Components.NewRecordingComponents

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.echojournal.R

@Composable
fun BottomBar(
    modifier: Modifier,
    isConfirmVisible: Boolean,
    isConfirmEnabled: Boolean,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current

    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
        Button(
            onClick = onCancel,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.sad_bg),
                contentColor = colorResource(R.color.cancel_color)
            ),
            modifier = Modifier
                .wrapContentWidth()
                .padding(start = 10.dp)
        ) {
            Text(text = context.getString(R.string.cancel))
        }

        Spacer(modifier.width(10.dp))

        if (isConfirmVisible) {
            Button(
                onClick = {
                    onConfirm()
                },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isConfirmEnabled) colorResource(R.color.confirm_color) else colorResource(
                        R.color.save_color
                    ),
                    contentColor = if (isConfirmEnabled) colorResource(R.color.white) else colorResource(
                        R.color.save_text
                    )
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 10.dp)
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = context.getString(R.string.save))
            }
        } else {
            Button(
                onClick = { onConfirm() },
                enabled = isConfirmEnabled,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isConfirmEnabled) colorResource(R.color.confirm_color) else colorResource(
                        R.color.save_color
                    ),
                    contentColor = if (isConfirmEnabled) colorResource(R.color.white) else colorResource(
                        R.color.save_text
                    )
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 10.dp)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "confirm")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = context.getString(R.string.confirm))
            }
        }
    }
}