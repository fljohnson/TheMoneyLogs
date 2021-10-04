package com.fouracessoftware.themoneylogs.data.roomy

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlannedTxnDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTxnWithCategory(txn: PlannedTxn,category: Category)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTxn(txn:PlannedTxn)

    @Query("SELECT * FROM planned")
    fun getAllTxns(): List<PlannedTxn>

    @Query("SELECT * FROM planned")
    fun getAllTxnsWithCategory(): Flow<List<TxnWithCategory>>
    @Query("SELECT * FROM planned WHERE txn_id = :id")
    fun getTxnWithCategory(id: Long): Flow<TxnWithCategory>
}