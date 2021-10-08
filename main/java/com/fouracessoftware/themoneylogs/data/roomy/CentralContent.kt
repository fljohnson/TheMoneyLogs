package com.fouracessoftware.themoneylogs.data.roomy



import android.icu.util.Calendar
import androidx.lifecycle.MutableLiveData
import com.fouracessoftware.themoneylogs.MainApplication
import com.fouracessoftware.themoneylogs.data.PrototypeContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

object CentralContent  {

    private var categoires = ArrayList<Category>(0)
    private var informant = MutableLiveData<ArrayList<Category>>()
    var plannedTxnDao: PlannedTxnDao
    var categoryDao:CategoryDao
    var planNoteDao: PlanNoteDao
    var actualTxnDao: ActualTxnDao

    private var categoriesReady = false
    private var plannedsReady = false

    init{
        informant.value = categoires
        categoryDao = AppDatabase.getInstance(MainApplication.getContext()).categoryDao()
        plannedTxnDao = AppDatabase.getInstance(MainApplication.getContext()).plannedTxnDao()
        planNoteDao = AppDatabase.getInstance(MainApplication.getContext()).planNoteDao()
        actualTxnDao = AppDatabase.getInstance(MainApplication.getContext()).actualTxnDao()
    }

    fun engage() {


        CoroutineScope(Dispatchers.IO).launch{

            categoryDao.insertCategory(Category(name="Housing"))
            categoryDao.insertCategory(Category(name = "Groceries", openEnded = true))
            categoryDao.insertCategory(Category(name = "Telecom"))
            categoryDao.insertCategory(Category(name = "Medical"))
            categoryDao.insertCategory(Category(name = "Health Insurance"))
            categoryDao.insertCategory(Category(name = "Transportation", openEnded = true,description = "gas, fares, tolls"))
            categoryDao.insertCategory(Category(name = "Life Insurance"))
            categoryDao.insertCategory(Category(name = "Debt", description = "non-revolving"))
            categoryDao.insertCategory(Category(name = "Bank Fees"))
            categoryDao.insertCategory(Category(name = "Credit Card"))
            categoryDao.insertCategory(Category(name = "Entertainment", openEnded = true,description = "food out, movies, events, museums"))
            getCategories()
            categoriesReady = true
            sproing()


        }

    }

    private fun insertPlanned(incoming:MutableMap<String,String>,
                              notes:List<MutableMap<String,String>>?=null,
                              actuals:List<MutableMap<String,String>>?=null
    ) {
        var category:Category? = null
        println("Grrr "+ categoires.size)
        for(c in categoires){
            if(c.name.contentEquals(incoming["Category"])){
                category = c
                break
            }
        }

        var duedate :Calendar? = null
        if(incoming["Date"]!!.isNotEmpty()) {
           duedate = getCalendarFor(incoming["Date"]!!)
        }


        println(category!!.name)
        if(incoming["Payee"] == "Citibank") {
            println("Darn")
        }

        val txn = PlannedTxn(amount = PrototypeContent.safeExtract(incoming["Amount"]),
            dateDue = duedate,
            payee = incoming["Payee"]!!,
            categoryId = category.categoryId
        )
       plannedTxnDao.insertTxnWithCategory(txn,category)
        val txnId = plannedTxnDao.getMaxId()
            println("AHA:$txnId")
        if(notes != null){
            for(noteData in notes){
                val planNote = PlanNote(xid=txnId,content=noteData["Content"])
                planNoteDao.insertNote(planNote)
            }
        }
        if(actuals != null){
            for(actualData in actuals) {
                val paiddate = getCalendarFor(actualData["Date"]!!)

                val actualTxn = ActualTxn(xid=txnId,
                    datePaid=paiddate,
                    amount=PrototypeContent.safeExtract(actualData["Amount"]))
                actualTxnDao.insertActualTxn(actualTxn)
            }
        }
    }
    private suspend fun getCategories() {

            categoires.addAll(categoryDao.getAllCategories().first())
    }

    private fun getCalendarFor(ymd:String):Calendar {
        val rv=Calendar.getInstance()

        val div1 = ymd.indexOf("-")
        val div2 = ymd.lastIndexOf("-")
        val yr = ymd.substring(0,div1).toInt()
        val mo = ymd.substring(div1+1,div2).toInt() - 1
        val da =ymd.substring(div2+1).toInt()
        rv.set(yr,mo,da)
        return rv
    }

    private fun sproing() {


            insertPlanned(
                mutableMapOf(
                    "Date" to "2021-09-03",
                    "Amount" to "827.3",
                    "Category" to "Housing",
                    "Payee" to "Rent"
                )
            )

            insertPlanned(
                mutableMapOf(
                    "Date" to "",
                    "Amount" to "120",
                    "Category" to "Debt",
                    "Payee" to "Citibank"
                )
            )
            insertPlanned(
                mutableMapOf(
                    "Date" to "2021-09-01",
                    "Amount" to "25.92",
                    "Category" to "Telecom",
                    "Payee" to "Gsuite"
                )
            )
            insertPlanned(
                mutableMapOf(
                    "Date" to "2021-09-01",
                    "Amount" to "50",
                    "Category" to "Debt",
                    "Payee" to "Midland Credit Mgmt"
                )
            )
            insertPlanned(
                mutableMapOf(
                    "Date" to "",
                    "Amount" to "",
                    "Category" to "Bank Fees",
                    "Payee" to "Wells Fargo"
                )
            )
            insertPlanned(
                mutableMapOf(
                    "Date" to "2021-09-02",
                    "Amount" to "",
                    "Category" to "Telecom",
                    "Payee" to "GoDaddy"
                )
            ,notes = listOf(mutableMapOf("Content" to "PayPal acct > $13.99 at the time"))
            )
            insertPlanned(
                mutableMapOf(
                    "Date" to "2021-09-06",
                    "Amount" to "19.95",
                    "Category" to "Telecom",
                    "Payee" to "Sisna.com"
                ),
                actuals = listOf(
                    mutableMapOf("xid" to "6","Date" to "2021-09-07","Amount" to "19.95")
                )

            )
            insertPlanned(
                mutableMapOf(
                    "Date" to "2021-09-10",
                    "Amount" to "20.1",
                    "Category" to "Life Insurance",
                    "Payee" to "Colonial Penn"
                ),
                actuals = listOf(mutableMapOf("xid" to "7","Date" to "2021-09-09","Amount" to "20.1"))

            )

            insertPlanned(
                mutableMapOf(
                    "Date" to "",
                    "Amount" to "90",
                    "Category" to "Groceries",
                    "Payee" to ""
                ),
               notes = listOf(
                    mutableMapOf("Content" to "Moved $44 plus $16 from last month to PFCU savings on 9/1")
                ),
                actuals = listOf(
                    mutableMapOf("xid" to "8","Date" to "2021-09-05","Amount" to "26.5"),
                    mutableMapOf("xid" to "8","Date" to "2021-09-09","Amount" to "34.49"),
                    mutableMapOf("xid" to "8","Date" to "2021-09-02","Amount" to "25.98")
                )
            )
            insertPlanned(
                mutableMapOf(
                    "Date" to "2021-09-17",
                    "Amount" to "",
                    "Category" to "Credit Card",
                    "Payee" to "Capital One"
                )
            )

            insertPlanned(
                mutableMapOf(
                    "Date" to "2021-09-07",
                    "Amount" to "40",
                    "Category" to "Medical",
                    "Payee" to "Therapy"
                )
            )

            insertPlanned(
                mutableMapOf(
                    "Date" to "",
                    "Amount" to "29",
                    "Category" to "Health Insurance",
                    "Payee" to "IBX"
                ),
               notes = listOf(
                    mutableMapOf("xid" to "11","Content" to "plus arrears;Actual is $57")
                )
            )

            insertPlanned(
                mutableMapOf(
                    "Date" to "2021-09-03",
                    "Amount" to "78.47",
                    "Category" to "Telecom",
                    "Payee" to "AT&T"
                )
            )
            insertPlanned(
                mutableMapOf(
                    "Date" to "2021-09-03",
                    "Amount" to "101.5",
                    "Category" to "Telecom",
                    "Payee" to "Comcast"
                )
            )
            insertPlanned(
                mutableMapOf(
                    "Date" to "2021-09-27",
                    "Amount" to "45",
                    "Category" to "Debt",
                    "Payee" to "PayPalCredit"
                ),
                notes = listOf(
                    mutableMapOf("xid" to "14","Content" to "Moved to high-yield savings, payment to be actually sent 3 days before due date")
                )
            )
            insertPlanned(
                mutableMapOf(
                    "Date" to "2021-09-03",
                    "Amount" to "65",
                    "Category" to "Medical",
                    "Payee" to "CVS"
                ),
                notes = listOf(mutableMapOf("xid" to "15","Content" to "Rx; correct figure")),
                actuals = listOf(mutableMapOf("xid" to "15","Date" to "2021-09-03","Amount" to "79.51"))
            )
            insertPlanned(
                mutableMapOf(
                    "Date" to "2021-09-10",
                    "Amount" to "25",
                    "Category" to "Debt",
                    "Payee" to "IRS"
                ),
                notes = listOf(
                    mutableMapOf("xid" to "16","Content" to "(2016 tax bill and 2018 tax bill) "),
                    mutableMapOf("xid" to "16","Content" to "Pushed into credit-card payment ($73 due 9/17)")
                )
            )
            insertPlanned(
                mutableMapOf(
                    "Date" to "2021-09-27",
                    "Amount" to "40",
                    "Category" to "Medical",
                    "Payee" to "Psychiatrist"
                ),
                notes = listOf(
                    mutableMapOf("xid" to "17","Content" to "actual due date seems to be 10 cal days in advance")
                )
            )
            insertPlanned(
                mutableMapOf(
                    "Date" to "2021-09-12",
                    "Amount" to "15",
                    "Category" to "Medical",
                    "Payee" to "Doctor visit"
                ),
                actuals =
                listOf(
                    mutableMapOf("xid" to "18","Date" to "2021-09-12","Amount" to "0")
                )
            )

        plannedsReady = true
    }


}