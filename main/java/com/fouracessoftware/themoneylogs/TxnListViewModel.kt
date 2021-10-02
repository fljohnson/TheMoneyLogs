package com.fouracessoftware.themoneylogs

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.fouracessoftware.themoneylogs.data.roomy.CentralContent
import com.fouracessoftware.themoneylogs.data.roomy.TxnWithCategory

class TxnListViewModel: ViewModel() {

    val txnList: LiveData<List<TxnWithCategory>> = CentralContent.plannedTxnDao.getAllTxnsWithCategory().asLiveData()
}