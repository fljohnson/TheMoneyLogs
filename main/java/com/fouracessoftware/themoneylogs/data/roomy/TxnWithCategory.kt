package com.fouracessoftware.themoneylogs.data.roomy

import android.icu.text.SimpleDateFormat


import androidx.room.Embedded
import androidx.room.Relation
import java.util.*

data class TxnWithCategory(
    @Embedded
    val txn:PlannedTxn,
    @Relation(parentColumn = "category_id",entityColumn = "id")
    val category:Category
) {
    val transactionTitle: CharSequence
        get() = run {
        val rv:String = if(amount()==null) {
            "(Unknown amount)"
        } else {
            "${amount()!!}"
        }
        "$rv for ${category.name} on ${dateDue()}"
    }


    fun getOutstandingAmount(): Float {
        if(txn.amount == null)
            return 0f
        return txn.amount
    }

    fun amount():Float? {
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



