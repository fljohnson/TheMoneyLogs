package com.fouracessoftware.themoneylogs.data.roomy

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlannedTxnDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTxnWithCategory(txn: PlannedTxn,category: Category)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTxn(txn:PlannedTxn)

    @Query("SELECT * FROM planned")
    fun getAllTxns(): List<PlannedTxn>

    @Transaction @Query("SELECT * FROM planned")
    fun getAllTxnsWithCategory(): Flow<List<TxnWithCategory>>
    @Transaction
    @Query("SELECT * FROM planned WHERE txn_id = :id")
    fun getTxnWithCategory(id: Long): Flow<TxnWithCategory>
    @Query("SELECT max(txn_id) from PLANNED")
    fun getMaxId():Long
}