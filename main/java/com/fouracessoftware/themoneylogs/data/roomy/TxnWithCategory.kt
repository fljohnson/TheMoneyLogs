package com.fouracessoftware.themoneylogs.data.roomy

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.room.ColumnInfo


import androidx.room.Embedded
import androidx.room.Relation
import java.util.*

data class TxnWithCategory(
    @Embedded
    val txn:PlannedTxn,
    @Relation(parentColumn = "category_id",entityColumn = "id")
    val category:Category
) {
    fun getOutstandingAmount(): Float {
        return txn.amount
    }

    fun amount():Float {
        return txn.amount
    }

    fun payee(): String {
        return txn.payee
    }

    fun dateDue(): String {
        if(txn.dateDue == null)
        {
            return "[Undated]"
        }
        return dateFormat.format(txn.dateDue)

    }


    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    }
}



