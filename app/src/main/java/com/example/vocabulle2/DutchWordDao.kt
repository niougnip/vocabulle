package com.example.vocabulle2

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DutchWordDao {
    @Query("SELECT * FROM dutchword")
    fun getAll(): List<DutchWord>

    @Query("SELECT * FROM dutchword LIMIT 1 OFFSET :offset")
    fun findByOffset(offset: Int): DutchWord

    @Query("SELECT count(*) FROM dutchword")
    fun countItems(): Int

    @Insert
    fun insertAll(vararg dutchWords: DutchWord)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(dutchWord: DutchWord)

    @Delete
    fun delete(dutchWord: DutchWord)

    @Query("SELECT * FROM dutchword WHERE french = :french")
    fun findItemFromFrench(french: String): DutchWord

    // het/de
    @Query("SELECT count(*) FROM dutchword WHERE dutch LIKE 'de %' OR dutch LIKE 'het %'")
    fun countArticleWords(): Int
    @Query("SELECT * FROM dutchword WHERE dutch LIKE 'de %' OR dutch LIKE 'het %' LIMIT 1 OFFSET :offset")
    fun findWithArticleByOffset(offset: Int): DutchWord
}