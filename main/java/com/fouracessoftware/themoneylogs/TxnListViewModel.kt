package com.fouracessoftware.themoneylogs

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.lifecycle.*
import androidx.room.Transaction
import com.fouracessoftware.themoneylogs.data.roomy.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class TxnListViewModel: ViewModel() {
    private val selected = MutableLiveData<TxnWithCategory>()
    fun select(item: TxnWithCategory) {
        selected.value = item
    }

    var message = MutableLiveData("")
    val txnList: LiveData<List<TxnWithCategory>> = CentralContent.plannedTxnDao.getAllTxnsWithCategory().asLiveData()
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

    fun update(item: TxnWithCategory, actual: ActualTxn? = null){
        CoroutineScope(Dispatchers.IO).launch {
            @Transaction
                if(actual!=null)
                    CentralContent.actualTxnDao.insertActualTxn(actual)
                CentralContent.plannedTxnDao.update(item.txn,item.category)

            message.postValue("OK")
        }


    }

    fun formatDate(cal: Calendar?): CharSequence? {
        return dateFormat.format(cal)
    }
    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    }
}
