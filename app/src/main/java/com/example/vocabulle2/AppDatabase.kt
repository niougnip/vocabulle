package com.example.vocabulle2

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DutchWord::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract val dao: DutchWordDao
}