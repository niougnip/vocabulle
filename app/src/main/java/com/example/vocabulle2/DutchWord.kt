package com.example.vocabulle2

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DutchWord(
    @PrimaryKey(autoGenerate = true) val uid: Int?,
    @ColumnInfo(name = "french") var french: String,
    @ColumnInfo(name = "dutch") val dutch: String
)