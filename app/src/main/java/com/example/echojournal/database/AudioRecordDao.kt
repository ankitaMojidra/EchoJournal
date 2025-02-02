package com.example.echojournal.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(audioRecord: AudioRecord): Long

    @Query("SELECT * FROM audio_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<AudioRecord>>

    @Query("SELECT * FROM audio_records WHERE id = :id")
    fun getRecordById(id: Int): Flow<AudioRecord?>

    @Update
    suspend fun update(audioRecord: AudioRecord)

    @Query("SELECT all_topics FROM audio_records")
    suspend fun getAllTopics(): List<String>
}