package com.fouracessoftware.themoneylogs.data.roomy

import android.icu.util.Calendar
import androidx.room.ColumnInfo


import androidx.room.Embedded
import androidx.room.Relation

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
        return "${txn.dateDue.get(Calendar.YEAR)}-${1+txn.dateDue.get(Calendar.MONTH)}-${txn.dateDue.get(Calendar.DATE)}"

    }
}



