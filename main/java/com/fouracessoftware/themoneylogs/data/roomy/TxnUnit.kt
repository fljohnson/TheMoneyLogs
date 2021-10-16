package com.fouracessoftware.themoneylogs.data.roomy

import android.icu.util.Calendar

abstract class TxnUnit {
    abstract var category: Category?
    abstract var payee: String
    abstract var amount: Float?
    abstract val transactionTitle: CharSequence?
    abstract fun dateDue(): String
    abstract fun setDateDue(date: Calendar)
    abstract var categoryId:Int
    /*
    abstract fun setCategoryInfo(c:Category)

    abstract fun getCategoryInfo(): Category?
    abstract fun setAmount(amt: Float)
    abstract fun setPayeeInfo(typedPayee: String)

     */

}