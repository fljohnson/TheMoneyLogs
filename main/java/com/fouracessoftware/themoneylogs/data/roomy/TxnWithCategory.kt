package com.fouracessoftware.themoneylogs.data.roomy

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar


import androidx.room.Embedded
import androidx.room.Relation
import java.util.*

data class TxnWithCategory(
    @Embedded
    val txn:PlannedTxn,
    @Relation(parentColumn = "category_id",entityColumn = "id")
    override var category: Category?,
    @Relation(parentColumn = "txn_id",entityColumn = "xid")
    val actuals:List<ActualTxn>
): TxnUnit() {
    override val transactionTitle: CharSequence
        get() = run {
        val rv:String = if(amount==null) {
            "(Unknown amount)"
        } else {
            "${amount!!}"
        }
        "$rv for ${category!!.name} on ${dateDue()}"
    }


    fun getOutstandingAmount(): Float {
        if(txn.amount == null)
            return 0f
        if(actuals.isNullOrEmpty()) {
            return txn.amount!!
        }
        var rv = txn.amount!!
        for(line in actuals) {
            rv -= line.amount
        }
        return rv
    }

    override var amount:Float? get() { return txn.amount}
     set(value) {txn.amount = value}


    override var payee: String get(){
        return txn.payee
    }
    set(value) {txn.payee = value}

    override fun dateDue(): String {
        if(txn.dateDue == null)
        {
            return "[Undated]"
        }
        return dateFormat.format(txn.dateDue)

    }

    override fun setDateDue(date: Calendar) {
        txn.dateDue = date
    }


    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    }



    override var categoryId: Int
        get() = txn.categoryId
        set(value) {txn.categoryId = value}




}



