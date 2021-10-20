package com.fouracessoftware.themoneylogs

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.icu.util.TimeZone
import androidx.lifecycle.*
import androidx.room.Transaction
import com.fouracessoftware.themoneylogs.data.roomy.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class TxnListViewModel: ViewModel() {

    var message = MutableLiveData("")
    var txnList: LiveData<List<TxnWithCategory>> = CentralContent.plannedTxnDao.getAllTxnsWithCategory().asLiveData()
    val categoryList: LiveData<List<Category>> = CentralContent.categoryDao.getAllCategories().asLiveData()
    fun getTxn(id:Long):LiveData<TxnWithCategory> {
        return CentralContent.plannedTxnDao.getTxnWithCategory(id).asLiveData()
    }
    fun getTxnPlanNotes(id:Long):LiveData<List<PlanNote>> {
        return CentralContent.planNoteDao.getNotesForTxn(id).asLiveData()
    }
    fun getActualTxnsForPlanned(id:Long):LiveData<List<ActualTxn>> {
        return CentralContent.actualTxnDao.getActualsForTxn(id).asLiveData()
    }

    fun update(item: TxnWithCategory, actual: ActualTxn? = null, note: PlanNote? = null){
        CoroutineScope(Dispatchers.IO).launch {
            @Transaction
                if(actual!=null)
                    CentralContent.actualTxnDao.insertActualTxn(actual)
            item.category?.let { CentralContent.plannedTxnDao.update(item.txn, it) }
                if(note!=null)
                    CentralContent.planNoteDao.insertNote(note)

            message.postValue("OK")
        }


    }

    fun formatDate(cal: Calendar?): CharSequence? {
        return dateFormat.format(cal)
    }

    fun insert(txn: PlannedTxn, category: Category, note: PlanNote?, actual: ActualTxn?) {
        CoroutineScope(Dispatchers.IO).launch {
            txn.categoryId = category.categoryId //belt and suspenders
            CentralContent.plannedTxnDao.insertTxnWithCategory(txn,category)
            val txnId = CentralContent.plannedTxnDao.getMaxId()

            @Transaction
            if(note != null){
                val planNote = PlanNote(xid=txnId,note.content)
                CentralContent.planNoteDao.insertNote(planNote)
            }
            if(actual != null){
                val actualTxn = ActualTxn(xid=txnId,
                    datePaid=actual.datePaid,
                    amount= actual.amount)
                CentralContent.actualTxnDao.insertActualTxn(actualTxn)
            }

            message.postValue("OK")
        }
    }

    fun getTxns(calendar: Calendar?) {
        val start=Calendar.getInstance(TimeZone.GMT_ZONE)
        val end=Calendar.getInstance(TimeZone.GMT_ZONE)

        if(calendar != null) {
            start.timeInMillis= calendar.timeInMillis
            end.timeInMillis = calendar.timeInMillis
        }

        start.set(Calendar.DAY_OF_MONTH,1)
        end.add(Calendar.MONTH,1)
        end.set(Calendar.DAY_OF_MONTH,0)

        txnList = CentralContent.plannedTxnDao.getRangedTxnsWithCategory(start,end).asLiveData()



    }

    fun copyTxns(fromCal: Calendar, toCal: Calendar) {
        val start = Calendar.getInstance(TimeZone.GMT_ZONE)
        val end = Calendar.getInstance(TimeZone.GMT_ZONE)

        start.time = fromCal.time
        end.time =fromCal.time

        start.set(Calendar.DAY_OF_MONTH,1)
        end.add(Calendar.MONTH,1)
        end.set(Calendar.DAY_OF_MONTH,0)

        val delta = fromCal.fieldDifference(toCal.time,Calendar.MONTH)
       /* println(dateFormat.format(start)+" to "+dateFormat.format(end))

        println(dateFormat.format(toCal))
        println(delta)*/

        CoroutineScope(Dispatchers.IO).launch {

            val oldList = CentralContent.plannedTxnDao.getNonliveRangedTxnsWithCategory(start,end)

            val ddate = Calendar.getInstance()
            for(t in oldList){
                var finishedDate:Calendar? = null
                if(t.txn.dateDue != null) {
                    ddate.time = t.txn.dateDue!!.time
                    ddate.add(Calendar.MONTH,delta)
                    finishedDate = ddate
                 //   println(dateFormat.format(finishedDate))
                }
                CentralContent.plannedTxnDao.insertTxnWithCategory(PlannedTxn(t.txn.amount,t.txn.payee,finishedDate,t.txn.categoryId),t.category!!)
            }
        }



        //val listToMove
    }

    fun delete(item: TxnWithCategory) {
        CoroutineScope(Dispatchers.IO).launch {
            CentralContent.plannedTxnDao.deleteTxn(item.txn,item.actuals)

        }
    }

    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    }
}
