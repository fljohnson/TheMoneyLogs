package com.fouracessoftware.themoneylogs.data.roomy

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import java.util.*

class ProtoTxnWithCategory :TxnUnit()
{
    private var _txnDateDue:Calendar? = null
    private var _txnCategoryId: Int= -1
    private var _category:Category? = null
    private  var _txnPayee :String =""
    private var _txnAmount= 0f
    override var category: Category?
        get() = _category
        set(value) {
            _category = value
            _txnCategoryId = _category!!.categoryId
        }

    override var payee: String
        get() = _txnPayee
        set(value) {_txnPayee = value}

    override var amount: Float?
        get() = _txnAmount
        set(value) {_txnAmount = value!!}

    override val transactionTitle: CharSequence
        get() = run {
            val catname:String = if(category == null) {
                "(Unknown category)"
            }
            else {
                category!!.name
            }
            val rv:String = if(amount==null) {
                "(Unknown amount)"
            } else {
                "${amount!!}"
            }

            "$rv for $catname on ${dateDue()}"
        }

    override fun dateDue(): String {
        return if(_txnDateDue == null) {
            "(Unknown date)"
        }
        else
        {
            dateFormat.format(_txnDateDue)
        }
    }

    override fun setDateDue(date: Calendar) {
        _txnDateDue = date
    }

    override var categoryId: Int
        get() = _txnCategoryId
        set(value) { _txnCategoryId = value }




    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    }

}
