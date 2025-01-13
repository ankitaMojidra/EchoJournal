package com.example.echojournal.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AudioRecord::class], version = 1, exportSchema = false)
abstract class AudioRecordDatabase : RoomDatabase() {
    abstract fun audioRecordDao(): AudioRecordDao

    companion object {
        @Volatile
        private var Instance: AudioRecordDatabase? = null

        fun getDatabase(context: Context): AudioRecordDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AudioRecordDatabase::class.java, "app_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}