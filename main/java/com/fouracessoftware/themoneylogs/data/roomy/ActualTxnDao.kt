package com.fouracessoftware.themoneylogs.data.roomy

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ActualTxnDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertActualTxn(actualTxn: ActualTxn)

    @Query("SELECT * FROM actual WHERE xid = :id")
    fun getActualsForTxn(id: Long): Flow<List<ActualTxn>>

}