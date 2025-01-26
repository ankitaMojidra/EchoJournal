package com.example.echojournal.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audio_records")
data class AudioRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    @ColumnInfo(name = "duration") val duration: Long,
    @ColumnInfo(name = "audio_data") val audioData: ByteArray,
    @ColumnInfo(name = "topic") val selectedTopic: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "mood") val mood: String,
    @ColumnInfo(name = "all_topics") val allTopics: String
)