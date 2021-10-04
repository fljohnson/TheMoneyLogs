package com.fouracessoftware.themoneylogs

import android.content.ClipData
import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.view.DragEvent
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.CollapsingToolbarLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.fouracessoftware.themoneylogs.data.PrototypeContent
import com.fouracessoftware.themoneylogs.data.roomy.Category
import com.fouracessoftware.themoneylogs.data.roomy.TxnWithCategory
import com.fouracessoftware.themoneylogs.databinding.FragmentItemDetailBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a [ItemListFragment]
 * in two-pane mode (on larger screen devices) or self-contained
 * on handsets.
 */
class ItemDetailFragment : Fragment(), Observer<List<Category>> {


    private var dateBtn: MaterialButton? = null
    private var chosenDate: String =""

    private val model: TxnListViewModel by viewModels()
    /**
     * The placeholder content this fragment is presenting.
     */
    private var item: TxnWithCategory? = null

    private lateinit var itemDetailTextView: TextView
    private var toolbarLayout: CollapsingToolbarLayout? = null

    private var _binding: FragmentItemDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    /* TODO remove
    private val dragListener = View.OnDragListener { _, event ->
        if (event.action == DragEvent.ACTION_DROP) {
            val clipDataItem: ClipData.Item = event.clipData.getItemAt(0)
            val dragData = clipDataItem.text
         //   item = PrototypeContent.items[dragData.toString().toInt()]
            updateContent()
        }
        true
    }
    */

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*
        item = model.selected.value
        model.selected.observe(viewLifecycleOwner, { selected_item ->
            item = selected_item
            // Update the UI
            updateContent()
        })*/

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                // Load the placeholder content specified by the fragment
                // arguments. In a real-world scenario, use a Loader
                // to load content from a content provider.
                model.getTxn(it.getLong(ARG_ITEM_ID)).observe(viewLifecycleOwner,Observer{
                        selected_item -> item = selected_item
                    // Update the UI
                    updateContent()
                })
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

/*
        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                // Load the placeholder content specified by the fragment
                // arguments. In a real-world scenario, use a Loader
                // to load content from a content provider.
                model.getTxn(it.getLong(ARG_ITEM_ID)).observe(viewLifecycleOwner,Observer{
                    selected_item -> item = selected_item
                    // Update the UI
                    updateContent()
                })
            }
        }*/

    }

    private val dateBtnListener = View.OnClickListener {v ->
        if(v==binding.plannedDate) {
            startDatePicker(true)
        }
        if(v==binding.actualDate) {
            startDatePicker(false)
        }
    }

    private fun startDatePicker(isPlanning: Boolean) {
        chosenDate = ""
        val whatToSay: String
        if(isPlanning) {
            whatToSay = R.string.lbl_set_date_paid.toString()
            dateBtn = binding.plannedDate
        }
        else {
            whatToSay = R.string.lbl_set_due_date.toString()
            dateBtn = binding.actualDate
        }
        val defaultDate = MaterialDatePicker.todayInUtcMilliseconds()
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(whatToSay)
                .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
                .setSelection(defaultDate)
                .build()
        //if the user commits to the date, get the value
        datePicker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = datePicker.selection!!
            chosenDate = "${calendar.get(Calendar.YEAR)}-${1+calendar.get(Calendar.MONTH)}-${1+calendar.get(Calendar.DAY_OF_MONTH)}"
        }

        datePicker.addOnDismissListener {
           if(chosenDate.isNotEmpty()){
               dateBtn?.text = chosenDate
               dateBtn = null
           }
        }
        //show it!
        datePicker.show(this.childFragmentManager,"tat")
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        _binding = FragmentItemDetailBinding.inflate(inflater, container, false)
        val rootView = binding.root

        toolbarLayout = binding.toolbarLayout
        itemDetailTextView = binding.itemNotes!!

        model.categoryList.observe(viewLifecycleOwner,this)

        binding.plannedDate?.setOnClickListener(dateBtnListener)
        binding.actualDate?.setOnClickListener(dateBtnListener)
        updateContent()
        //rootView.setOnDragListener(dragListener)

        return rootView
    }

    private fun updateContent() {
        toolbarLayout?.title = item?.transactionTitle

        // Show the placeholder content as text in a TextView.
        item?.let {
           // itemDetailTextView.text = it.getNotes() TODO
            var toShow = it.amount().toString()
            var fullAmt = it.amount()
            var remainingAmt = it.getOutstandingAmount()
            if(remainingAmt <0f) {
                /* TODO once the actuals data structure exists
                toShow = (it.getTotalPaid())
                binding.plannedDate?.setText("Paid")
                binding.plannedDate?.isEnabled = false
                */
            }
            else {
                toShow = it.getOutstandingAmount().toString()
                if(it.dateDue().trim().isNotEmpty()) {
                    binding.plannedDate?.text = it.dateDue()
                }
                binding.plannedDate?.isEnabled = true
            }
            binding.plannedAmount?.setText(toShow)
            binding.payee?.setText(it.payee())

            (binding.categoryMenu!!.editText as? AutoCompleteTextView)?.setText(it.category.name,false)
        }
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun onPlannedDateClick() {

    }
    fun onActualDateClick() {

    }

    override fun onChanged(t: List<Category>?) {
        /*
        categoryList.clear()
        categoryList.addAll(t!!)
        val items = ArrayList<String>() //model.categoryList.value!!
        for(c in categoryList!!){
            items.add(c.name)
        }
        */
        val adapter = CategoryAdapter(requireContext(), R.layout.category_menu_item,model.categoryList.value)

        (binding.categoryMenu!!.editText as? AutoCompleteTextView)?.setAdapter(adapter)

    }

    class CategoryAdapter(
        context: Context,
        resource: Int,
        //objects: List<String>,
        categoryList: List<Category>?
    ) :
        ArrayAdapter<Category>(context, resource, categoryList!!) {
        private val resource:Int = resource
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var theView = convertView
            if(theView == null) {
                theView = LayoutInflater.from(context).inflate(resource,parent,false)

            }
            //"if content == null, return theView" (FLJ, 10/3/2021)
            val content = getItem(position) ?: return theView!!
            /*
            if(categoryList == null)
                return theView!!
            val suspect = categoryList[position]
            if(!.description.isNullOrEmpty())

             */

            var labelText = content.name
            if(!content.description.isNullOrEmpty())
            {
                labelText += " ("+content.description+")"
            }
            theView!!.findViewById<TextView>(R.id.txView).text = labelText


            return theView
        }
    }
}