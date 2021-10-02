package com.fouracessoftware.themoneylogs.data

//import android.icu.text.NumberFormat
import java.util.ArrayList

object PrototypeContent {
    //private val numFmt = NumberFormat.getInstance()

    private val plannedTbl = ArrayList<Map<String,String>>()

    private val notesTbl = ArrayList<Map<String,String>>()

    private val categoriesTbl = ArrayList<Map<String,String>>()
    private val actualTbl = ArrayList<Map<String,String>>()
    init {
        plannedTbl.add(mutableMapOf("Date" to "2021-09-03","Amount" to "827.3", "Category" to "Housing", "Payee" to "Rent"))
        plannedTbl.add(mutableMapOf("Date" to 	"","Amount" to "120", "Category" to "Debt", "Payee" to "Citibank"))
        plannedTbl.add(mutableMapOf("Date" to "2021-09-01","Amount" to "25.92", "Category" to "Telecom", "Payee" to "Gsuite"))
        plannedTbl.add(mutableMapOf("Date" to "2021-09-01","Amount" to "50", "Category" to "Debt", "Payee" to "Midland Credit Mgmt"))
        plannedTbl.add(mutableMapOf("Date" to "","Amount" to "", "Category" to "Bank Fees", "Payee" to "Wells Fargo"))
        plannedTbl.add(mutableMapOf("Date" to "2021-09-02","Amount" to "", "Category" to "Telecom", "Payee" to "GoDaddy"))
        plannedTbl.add(mutableMapOf("Date" to "2021-09-06","Amount" to "19.95", "Category" to "Telecom", "Payee" to "Sisna.com"))
        plannedTbl.add(mutableMapOf("Date" to "2021-09-10","Amount" to "20.1", "Category" to "Life Insurance", "Payee" to "Colonial Penn"))
        plannedTbl.add(mutableMapOf("Date" to "","Amount" to "90", "Category" to "Groceries", "Payee" to ""))
        plannedTbl.add(mutableMapOf("Date" to "2021-09-17","Amount" to "", "Category" to "Credit Card", "Payee" to "Capital One"))
        plannedTbl.add(mutableMapOf("Date" to "2021-09-07","Amount" to "40", "Category" to "Medical", "Payee" to "Therapy"))
        plannedTbl.add(mutableMapOf("Date" to "","Amount" to "29", "Category" to "Health Insurance", "Payee" to "IBX"))
        plannedTbl.add(mutableMapOf("Date" to "2021-09-03","Amount" to "78.47", "Category" to "Telecom", "Payee" to "AT&T"))
        plannedTbl.add(mutableMapOf("Date" to "2021-09-03","Amount" to "101.5", "Category" to "Telecom", "Payee" to "Comcast"))
        plannedTbl.add(mutableMapOf("Date" to "2021-09-27","Amount" to "45", "Category" to "Debt", "Payee" to "PayPalCredit"))
        plannedTbl.add(mutableMapOf("Date" to "2021-09-03","Amount" to "65", "Category" to "Medical", "Payee" to "CVS"))
        plannedTbl.add(mutableMapOf("Date" to "2021-09-10","Amount" to "25", "Category" to "Debt", "Payee" to "IRS"))
        plannedTbl.add(mutableMapOf("Date" to "2021-09-27","Amount" to "40", "Category" to "Medical", "Payee" to "Psychiatrist"))
        plannedTbl.add(mutableMapOf("Date" to "2021-09-12","Amount" to "15", "Category" to "Medical", "Payee" to "Doctor visit"))


        notesTbl.add(mutableMapOf("xid" to "11","Content" to "plus arrears;Actual is $57"))
        notesTbl.add(mutableMapOf("xid" to "15","Content" to "Rx; correct figure"))
        notesTbl.add(mutableMapOf("xid" to "16","Content" to "(2016 tax bill and 2018 tax bill) "))
        notesTbl.add(mutableMapOf("xid" to "17","Content" to "actual due date seems to be 10 cal days in advance"))
        notesTbl.add(mutableMapOf("xid" to "16","Content" to "Pushed into credit-card payment ($73 due 9/17)"))
        notesTbl.add(mutableMapOf("xid" to "14","Content" to "Moved to high-yield savings, payment to be actually sent 3 days before due date"))
        notesTbl.add(mutableMapOf("xid" to "8","Content" to "Moved $44 plus $16 from last month to PFCU savings on 9/1"))
        notesTbl.add(mutableMapOf("xid" to "5","Content" to "PayPal acct > $13.99 at the time"))
//notesTbl.add(mutableMapOf("xid" to "15","Content" to "Rx plus shavers and same-day delivery"))
//notesTbl.add(mutableMapOf("xid" to "18","Content" to "Actual figure was $0"))

        categoriesTbl.add(mutableMapOf("Name" to "Housing"))
        categoriesTbl.add(mutableMapOf("Name" to "Groceries", "IsOpen" to "y"))
        categoriesTbl.add(mutableMapOf("Name" to "Telecom"))
        categoriesTbl.add(mutableMapOf("Name" to "Medical"))
        categoriesTbl.add(mutableMapOf("Name" to "Health Insurance"))
        categoriesTbl.add(mutableMapOf("Name" to "Transportation", "IsOpen" to "y","Description" to "gas, fares, tolls"))
        categoriesTbl.add(mutableMapOf("Name" to "Life Insurance"))
        categoriesTbl.add(mutableMapOf("Name" to "Debt", "Description" to "non-revolving"))
        categoriesTbl.add(mutableMapOf("Name" to "Bank Fees"))
        categoriesTbl.add(mutableMapOf("Name" to "Credit Card"))
        categoriesTbl.add(mutableMapOf("Name" to "Entertainment", "IsOpen" to "y","Description" to "food out, movies,events, museums"))

        actualTbl.add(mutableMapOf("xid" to "18","Date" to "2021-09-12","Amount" to "0"))
        actualTbl.add(mutableMapOf("xid" to "15","Date" to "2021-09-03","Amount" to "79.51"))
        actualTbl.add(mutableMapOf("xid" to "6","Date" to "2021-09-07","Amount" to "19.95"))
        actualTbl.add(mutableMapOf("xid" to "7","Date" to "2021-09-09","Amount" to "20.1"))
        actualTbl.add(mutableMapOf("xid" to "8","Date" to "2021-09-05","Amount" to "26.5"))	//Groceries
        actualTbl.add(mutableMapOf("xid" to "8","Date" to "2021-09-09","Amount" to "34.49"))	//Groceries
        actualTbl.add(mutableMapOf("xid" to "8","Date" to "2021-09-02","Amount" to "25.98"))	//Groceries

    }

    val items get() = fetchAll()


    private fun fetchAll(): ArrayList<PrototypeItem> {
        val rv: ArrayList<PrototypeItem> = ArrayList()
        for (i in 0 until plannedTbl.size) {
            rv.add(PrototypeItem(i, plannedTbl[i]))

        }
        return rv
    }

    fun getCategories(): List<String> {
        val rv= ArrayList<String>()
        for(i:Map<String,String> in categoriesTbl) {
            rv.add(i["Name"]!!)
        }
        return rv
    }

    /**
     * A placeholder item representing a piece of content.
     */
    data class PrototypeItem(val id:Int,private val tuple:Map<String,String>) {
        fun getNotes(): CharSequence {
           var rv=""
            for(i:Map<String,String> in notesTbl) {
                if(i["xid"]!!.toInt() == id) {
                    if(i["Content"].isNullOrEmpty()) {
                        continue
                    }
                    if(rv.isNotEmpty()){
                        rv+="\r\n"
                    }
                    rv+=i["Content"]!!.trim()
                }
            }
            return rv
        }

        // override fun toString(): String = content
        val amount get() = tuple["Amount"].toString()
        val category get() = tuple["Category"]
        val date get() = tuple["Date"]
        val payee get() = tuple["Payee"]
        val transactionTitle get() = run {
            val rv:String = if(tuple["Amount"].isNullOrEmpty()) {
                "(Unknown amount)"
            } else {
                tuple["Amount"]!!
            }
            "$rv for $category on $date"
        }

        fun getOutstandingAmount(): String {
            var base=safeExtract(tuple["Amount"])
            for(i:Map<String,String> in actualTbl) {
                if(i["xid"]!!.toInt() == id) {
                    if(i["Amount"].isNullOrEmpty()) {
                        continue
                    }
                    base -= safeExtract(i["Amount"])
                }
            }
            return ((base*100).toInt()/100f).toString()
        }

        fun getTotalPaid(): String {
            //TODO: if this.date is empty, get all of that category's transactions
            var base = 0f;
            for(i:Map<String,String> in actualTbl) {
                if(i["xid"]!!.toInt() == id) {
                    if(i["Amount"].isNullOrEmpty()) {
                        continue
                    }
                    base += safeExtract(i["Amount"])
                }
            }
            return ((base*100).toInt()/100f).toString()
        }
    }

    fun safeExtract(s: String?): Float {
        if(s.isNullOrEmpty()) {
            return 0f
        }
        return s.trim().toFloat()
    }

}