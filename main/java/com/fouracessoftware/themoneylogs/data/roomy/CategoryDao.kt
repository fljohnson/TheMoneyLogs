package com.fouracessoftware.themoneylogs.data.roomy

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategory(category: Category):Long

    @Query("SELECT * FROM category WHERE id = :categoryId")
    fun getCategory(categoryId:Int) : Flow<Category>
    @Query("SELECT * FROM category")
    fun getAllCategories() :Flow<List<Category>>
}