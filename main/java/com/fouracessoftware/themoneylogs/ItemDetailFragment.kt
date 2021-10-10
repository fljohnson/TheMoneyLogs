package com.fouracessoftware.themoneylogs

import android.content.Context
import android.content.DialogInterface
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.CollapsingToolbarLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.fouracessoftware.themoneylogs.data.roomy.ActualTxn
import com.fouracessoftware.themoneylogs.data.roomy.Category
import com.fouracessoftware.themoneylogs.data.roomy.PlanNote
import com.fouracessoftware.themoneylogs.data.roomy.TxnWithCategory
import com.fouracessoftware.themoneylogs.databinding.FragmentItemDetailBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a [ItemListFragment]
 * in two-pane mode (on larger screen devices) or self-contained
 * on handsets.
 */
class ItemDetailFragment : Fragment(), Observer<List<Category>> {


    private var actualTxns: List<ActualTxn>? = null
    private var dateBtn: MaterialButton? = null
    private var chosenDate: String =""
    private var notes =""

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



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                // Load the placeholder content specified by the fragment
                // arguments. In a real-world scenario, use a Loader
                // to load content from a content provider.
                model.getTxn(it.getLong(ARG_ITEM_ID)).observe(viewLifecycleOwner, {
                        selected_item -> item = selected_item
                    // Update the UI
                    updateContent()
                })
                model.getTxnPlanNotes(it.getLong(ARG_ITEM_ID)).observe(viewLifecycleOwner, {
                    notesList -> notes=""
                    for(line in notesList){
                        if(notes.isNotEmpty())
                        {
                            notes+="\r\n"
                        }
                        notes+="-"+line.content
                    }
                    updateContent()
                })
                model.getActualTxnsForPlanned(it.getLong(ARG_ITEM_ID)).observe(viewLifecycleOwner,{
                    actualsList -> actualTxns = actualsList
                    updateContent()
                })
            }
        }

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

        binding.detailToolbar?.setNavigationOnClickListener({
            MaterialAlertDialogBuilder(requireContext())
                .setMessage("Changes will not be saved")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", DialogInterface.OnClickListener { v, i ->
                    findNavController().navigateUp()
                })
                .show()
        })

        updateContent()


        return rootView
    }

    private fun updateContent() {
      //  toolbarLayout?.title = item?.transactionTitle
        binding.txnTitle?.text = item?.transactionTitle
        //setIcon(android.R.drawable.ic_menu_close_clear_cancel)
        // Show the placeholder content as text in a TextView.
        item?.let {
           // itemDetailTextView.text = it.getNotes() TODO
            val fullAmt = it.amount()
            var toShow = fullAmt.toString()
            val remainingAmt = getOutstandingAmount(it)
            if(remainingAmt <=0f) {
                //TODO once the actuals data structure exists
                toShow = getTotalPaid(it).toString()
                binding.plannedDate?.text = "Paid"
                binding.plannedDate?.isEnabled = false
                binding.actualDate?.text = "Paid"
                binding.actualDate?.isEnabled = false

            }
            else {
                toShow = getOutstandingAmount(it).toString()
                if(it.dateDue().trim().isNotEmpty()) {
                    binding.plannedDate?.text = it.dateDue()
                }
                binding.plannedDate?.isEnabled = true
            }
            binding.plannedAmount?.setText(toShow)
            binding.payee?.setText(it.payee())

            (binding.categoryMenu!!.editText as? AutoCompleteTextView)?.setText(it.category.name,false)
        }

        var toto=notes
        if(actualTxns != null) {
            for(line in actualTxns!!) {
                if(toto.isNotEmpty()) {
                    toto+="\r\n"
                }
                toto+="${line.datePaid()}:${line.amount.toString()} paid"
            }
        }

        binding.itemNotes?.text = toto
    }

    private fun getTotalPaid(it: TxnWithCategory):Float {
        var amt = 0f
        if(actualTxns != null) {
            for(tx in actualTxns!!) {
                amt += tx.amount
            }
        }
        return amt
    }
    private fun getOutstandingAmount(it: TxnWithCategory): Float {
        var amt = it.getOutstandingAmount()
        amt -=getTotalPaid(it)
        return amt
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


    override fun onChanged(t: List<Category>?) {

        val adapter = CategoryAdapter(requireContext(), R.layout.category_menu_item,model.categoryList.value)

        (binding.categoryMenu!!.editText as? AutoCompleteTextView)?.setAdapter(adapter)

    }

    class CategoryAdapter(
        context: Context,
        private val resource: Int,
        categoryList: List<Category>?
    ) :
        ArrayAdapter<Category>(context, resource, categoryList!!) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var theView = convertView
            if(theView == null) {
                theView = LayoutInflater.from(context).inflate(resource,parent,false)

            }
            //"if content == null, return theView" (FLJ, 10/3/2021)
            val content = getItem(position) ?: return theView!!


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