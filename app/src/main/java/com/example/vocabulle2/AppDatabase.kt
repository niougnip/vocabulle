package com.example.vocabulle2

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TranslationEntity::class], version = 1, exportSchema = true)
public abstract class AppDatabase : RoomDatabase() {
    abstract val dao: TranslationDao
}