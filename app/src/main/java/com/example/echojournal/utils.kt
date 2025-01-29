package com.example.echojournal

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

fun formatDuration(durationMillis: Long): String {
    val totalSeconds = durationMillis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}


@RequiresApi(Build.VERSION_CODES.O)
fun getRelativeDay(timestamp: Long): String {
    val instant = Instant.ofEpochMilli(timestamp)
    val timestampDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)

    return when (timestampDate) {
        today -> "TODAY"
        yesterday -> "YESTERDAY"
        else -> {
            val daysAgo = ChronoUnit.DAYS.between(timestampDate, today)
            if (daysAgo < 7)
                "$daysAgo DAYS AGO"
            else
                timestampDate.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy"))
        }
    }
}


fun getDate(timestamp: Long): String {
    val formattedDate =
        SimpleDateFormat(
            "hh:mm a",
            Locale.getDefault()
        ).format(Date(timestamp))
    return formattedDate
}