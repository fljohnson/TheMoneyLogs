package com.fouracessoftware.themoneylogs

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fouracessoftware.themoneylogs.data.PrototypeContent
import com.fouracessoftware.themoneylogs.data.roomy.Category
import com.fouracessoftware.themoneylogs.data.roomy.CentralContent
import com.fouracessoftware.themoneylogs.data.roomy.PlannedTxn
import com.fouracessoftware.themoneylogs.data.roomy.TxnWithCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class ItemViewModel: ViewModel() {


    val categoryList = MutableLiveData<List<Category>>()
    val txnList = MutableLiveData<ArrayList<PlannedTxn>>()
    val txnExtraList = MutableLiveData<ArrayList<TxnWithCategory>>()
    private var categoryListFlow: Flow<List<Category>> = CentralContent.categoryDao.getAllCategories()
   // private var txnListFlow: Flow<List<TxnWithCategory>> = CentralContent.plannedTxnDao.getAllTxns()
    val selected = MutableLiveData<TxnWithCategory>()
    fun select(item: TxnWithCategory) {
        selected.value = item
    }


    fun getItems(): ArrayList<PrototypeContent.PrototypeItem> {
        return PrototypeContent.items
    }

    fun getCategories2() :List<String> {
        return PrototypeContent.getCategories()
    }

    fun loadCategories(){
        CoroutineScope(Dispatchers.IO).launch {
            categoryListFlow.collect {
                CoroutineScope(Dispatchers.Main).launch {
                    categoryList.value = it

                }
            }
        }
    }

    fun loadTxns(){
        var sospecho:List<TxnWithCategory> = ArrayList()

        CoroutineScope(Dispatchers.IO).launch {
            txnList.value!!.clear()
            txnList.value!!.addAll(CentralContent.plannedTxnDao.getAllTxns())

            if(! txnList.value!!.isEmpty())
            {
                println("aha")
            }
        }
    }

    fun loadTxnsExtra() {
        CoroutineScope(Dispatchers.IO).launch {
            txnExtraList.value!!.clear()
           // txnExtraList.value!!.addAll(CentralContent.plannedTxnDao.getAllTxnsWithCategory())

            if(! txnExtraList.value!!.isEmpty())
            {
                println("aha")
            }
        }
    }



    init {
        txnList.value = ArrayList()
        categoryList.value = ArrayList()
        txnExtraList.value = ArrayList()

        CoroutineScope(Dispatchers.Main).launch {
            while(!CentralContent.ready()){

            }
            loadTxnsExtra()
            /*
            loadCategories()
            loadTxns()*/
        }
    }

    companion object {
        private val dateFormat = SimpleDateFormat("mm/dd/yyyy", Locale.US)
    }

}
