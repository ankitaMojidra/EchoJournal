package com.example.echojournal.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioRecordDao {
    @Insert
    suspend fun insert(audioRecord: AudioRecord)

    @Query("SELECT * FROM audio_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<AudioRecord>>
}