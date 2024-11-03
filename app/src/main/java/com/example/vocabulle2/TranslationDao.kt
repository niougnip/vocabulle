package com.example.vocabulle2

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TranslationDao {
    @Query("SELECT * FROM TranslationEntity WHERE isoCode = :isoCode")
    fun getAll(isoCode: String): List<TranslationEntity>

    @Query("SELECT * FROM TranslationEntity WHERE isoCode = :isoCode LIMIT 1 OFFSET :offset")
    fun findByOffset(isoCode: String, offset: Int): TranslationEntity

    @Query("SELECT count(*) FROM TranslationEntity WHERE isoCode = :isoCode")
    fun countItems(isoCode: String): Int

    @Insert
    fun insertAll(vararg translationEntities: TranslationEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(translationEntity: TranslationEntity)

    @Delete
    fun delete(translationEntity: TranslationEntity)

    @Query("SELECT DISTINCT(isoCode) FROM TranslationEntity")
    fun getIsoCodes(): List<String>

    @Query("SELECT * FROM TranslationEntity WHERE french = :french AND isoCode = :isoCode")
    fun findItemFromFrench(french: String, isoCode: String): TranslationEntity

    // het/de
    @Query("SELECT count(*) FROM TranslationEntity WHERE (other LIKE 'de %' OR other LIKE 'het %') AND isoCode = 'NL'")
    fun countArticleWords(): Int
    @Query("SELECT * FROM TranslationEntity WHERE (other LIKE 'de %' OR other LIKE 'het %') AND isoCode = 'NL' LIMIT 1 OFFSET :offset")
    fun findWithArticleByOffset(offset: Int): TranslationEntity
}