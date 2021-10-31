package com.fouracessoftware.themoneylogs.data.roomy

import android.icu.util.Calendar
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

    @Query("SELECT actual.* FROM actual JOIN planned ON planned.txn_id = actual.xid WHERE category_id == :categoryId AND datePaid >= :firstDate AND datePaid <= :lastDate")
    fun getActualsForCategory(categoryId: Int, firstDate: Calendar, lastDate: Calendar): Flow<List<ActualTxn>>

}