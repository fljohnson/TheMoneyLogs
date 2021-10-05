package com.fouracessoftware.themoneylogs.data.roomy

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanNoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNote(planNote: PlanNote)

    @Query("SELECT * FROM plan_note WHERE xid = :id")
    fun getNotesForTxn(id: Long): Flow<List<PlanNote>>
}