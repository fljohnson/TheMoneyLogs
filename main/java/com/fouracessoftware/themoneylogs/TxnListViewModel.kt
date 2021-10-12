package com.fouracessoftware.themoneylogs

import androidx.lifecycle.*
import com.fouracessoftware.themoneylogs.data.roomy.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class TxnListViewModel: ViewModel() {
    private val selected = MutableLiveData<TxnWithCategory>()
    fun select(item: TxnWithCategory) {
        selected.value = item
    }

    var message = MutableLiveData<String>("")
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

    fun update(item: TxnWithCategory){
        CoroutineScope(Dispatchers.IO).launch {

            CentralContent.plannedTxnDao.update(item.txn,item.category)
            message.postValue("OK")
        }


    }
}
