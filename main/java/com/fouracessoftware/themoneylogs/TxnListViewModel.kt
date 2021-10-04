package com.fouracessoftware.themoneylogs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.fouracessoftware.themoneylogs.data.roomy.Category
import com.fouracessoftware.themoneylogs.data.roomy.CentralContent
import com.fouracessoftware.themoneylogs.data.roomy.TxnWithCategory

class TxnListViewModel: ViewModel() {
    val selected = MutableLiveData<TxnWithCategory>()
    fun select(item: TxnWithCategory) {
        selected.value = item
    }

    val txnList: LiveData<List<TxnWithCategory>> = CentralContent.plannedTxnDao.getAllTxnsWithCategory().asLiveData()
    val categoryList: LiveData<List<Category>> = CentralContent.categoryDao.getAllCategories().asLiveData()
    fun getTxn(id:Long):LiveData<TxnWithCategory> {
        return CentralContent.plannedTxnDao.getTxnWithCategory(id).asLiveData()
    }
}
