package com.fouracessoftware.themoneylogs

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.CollapsingToolbarLayout
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.fouracessoftware.themoneylogs.data.roomy.*
import com.fouracessoftware.themoneylogs.databinding.FragmentItemDetailBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.util.*

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a [ItemListFragment]
 * in two-pane mode (on larger screen devices) or self-contained
 * on handsets.
 */
class ItemDetailFragment : Fragment(), Observer<List<Category>> {


    private var actualTxns: List<ActualTxn>? = null
    private var dateBtn: MaterialButton? = null
    private var notes =""

    private val model: TxnListViewModel by viewModels()

    /**
     * The placeholder content this fragment is presenting.
     */
    private var item: TxnUnit? = null
    private var changeCounter:TxnWithCategory? = null
    private var categoryIndex:Int = -1

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
                val txnID = it.getLong(ARG_ITEM_ID)
                if(txnID == -1L) {
                    //item = ProtoTxnWithCategory()
                    changeCounter = null
                    updateContent()
                    return

                }

                model.getTxn(txnID).observe(viewLifecycleOwner, {
                        selected_item -> item = selected_item
                       // changeCounter = (item as TxnWithCategory?)?.copy() //apparently a deep copy
                    (item as TxnWithCategory).let { t ->

                        changeCounter = TxnWithCategory(
                            t.txn.copy(),
                            t.category,
                            t.actuals

                        )
                    }

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


        val whatToSay: String
        if(isPlanning) {
            whatToSay = getString(R.string.lbl_set_due_date)
            dateBtn = binding.plannedDate
        }
        else {
            whatToSay = getString(R.string.lbl_set_date_paid)
            dateBtn = binding.actualDate
        }

        var defaultDate = MaterialDatePicker.todayInUtcMilliseconds() //what MDP thinks is the correct time
        val currentDate = getCalendarForDate(dateBtn?.text!!)
        if(currentDate != null) {
            defaultDate = currentDate.timeInMillis

        }
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(whatToSay)
                .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
                .setSelection(defaultDate)
                .build()
        //if the user commits to the date, get the value
        datePicker.addOnPositiveButtonClickListener {
            //the MaterialDatePicker apparently "thinks" in UTC (FLJ, 10/18/2021)
            val calendar = Calendar.getInstance(TimeZone.GMT_ZONE)
            calendar.timeInMillis =  it
            TxnListViewModel.makeMidnight(calendar)

            //chosenDate = "${calendar.get(Calendar.YEAR)}-${1+calendar.get(Calendar.MONTH)}-${1+calendar.get(Calendar.DAY_OF_MONTH)}"
            if(isPlanning) {
                if (item == null)
                    dateBtn?.text = model.formatDate(calendar)
                item?.setDateDue(calendar)
            }
            else
                dateBtn?.text = model.formatDate(calendar)
            updateContent()
        }

        datePicker.addOnDismissListener {
            dateBtn=null
        }
        //show it!
        datePicker.show(this.childFragmentManager,"tat")
    }

    private fun getCalendarForDate(textdate:CharSequence):Calendar? {
        val outdate = Calendar.getInstance(TimeZone.GMT_ZONE)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        //now here's a neat Kotlin construct: a try-catch block can be an expression
        return try {
            outdate.time = dateFormat.parse(textdate.toString())
            //TxnListViewModel.makeMidnight(outdate)
            outdate
        } catch (ecch:Exception) {
            null
        }
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

        binding.detailToolbar?.setNavigationOnClickListener {
            if(!diffFromOriginal())
            {
                findNavController().navigateUp()
            }
            else
            {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage("Changes will not be saved ")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("OK") { _, _ ->
                        findNavController().navigateUp()
                    }
                    .show()

            }
        }

        binding.detailToolbar?.setOnMenuItemClickListener { x ->

            when (x.itemId) {
                R.id.save -> {
                    beginSave()
                    true
                }
                else -> {
                    false
                }
            }
        }

        (binding.categoryMenu!!.editText as AutoCompleteTextView).setOnItemClickListener { _: AdapterView<*>, _: View, i: Int, _: Long ->

            val adapteur = (binding.categoryMenu!!.editText as AutoCompleteTextView).adapter as CategoryAdapter
            categoryIndex = i
            item?.let {
                it.category = adapteur.getItem(i)!!
                it.categoryId = it.category!!.categoryId
            }
            if(adapteur.getItem(i)!!.openEnded) {
                var month = getCalendarForDate(binding.plannedDate!!.text)
                if(month == null)
                {
                    month = getCalendarForDate(binding.actualDate!!.text)
                }
                model.getActualTxnsForCategory(adapteur.getItem(i)!!.categoryId,month).observe(viewLifecycleOwner,{
                        actualsList -> actualTxns = actualsList
                    updateContent()
                })
            }
            //and finally
            updateContent()
        }




        return rootView
    }

    private fun diffFromOriginal(): Boolean {
        if(changeCounter == null) {
            return true
        }

        changeCounter?.let {
            //did the user add a note?
            if(binding.itemAddenda?.text!=null) {
                if(binding.itemAddenda?.text!!.trim().isNotEmpty())
                    return true
            }
            //if the user blanked out the payee field, don't count it as a change
            val typedPayee = binding.payee?.text!!.trim().toString() //so we'll be comparing apples to apples
            if(typedPayee.isNotEmpty()) {
                if(it.payee != typedPayee) {
                    return true
                }
            }
            if( !item?.category?.openEnded!!) {
                if (it.amount.toString() != binding.plannedAmount?.text.toString())
                    return true
            }

            if(item?.category?.categoryId != it.category?.categoryId)
                return true

            //did the user change the Due Date?

            if(item?.dateDue() != it.dateDue() ) {
                return true
            }
            val datePaid = getCalendarForDate(binding.actualDate?.text.toString())
            if(datePaid != null) {
                val amountPaid = binding.actualAmount?.text.toString()
                if(amountPaid.isNotEmpty()) {
                    return true
                }
            }
        }



        return false
    }

    inner class Bailor : Snackbar.Callback() {
        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
            super.onDismissed(transientBottomBar, event)
            findNavController().navigateUp()
        }
    }

    private fun reportError(msg:String) {
        binding.root.let { v ->
            Snackbar.make(v, msg, Snackbar.LENGTH_SHORT)
                .show()
        }
    }
    private fun beginSave() {
        if(item == null)
        {
            //get the final values from the UI elements
            val amountStr = binding.plannedAmount?.text.toString()

            val amount = if(amountStr.isBlank()) {
                0f
            }
            else
            {
                amountStr.toFloat()
            }

            val payee = binding.payee?.text!!.trim().toString()
            if(payee.isBlank()) {
                reportError("Please enter a payee or payor")
                return
            }
            var dateDue = binding.plannedDate?.text?.let { getCalendarForDate(it) }
            val adapteur = (binding.categoryMenu!!.editText as AutoCompleteTextView).adapter as CategoryAdapter

            if(categoryIndex == -1)
            {
                reportError("Please choose a category")
                return
            }

            val datePaid = getCalendarForDate(binding.actualDate?.text.toString())
            val category = adapteur.getItem(categoryIndex)!!

            if (category.openEnded) {
                if ((datePaid != null) && (dateDue == null)) {
                    dateDue = getCalendarForDate(binding.actualDate?.text.toString())
                }
            }

            val txn = PlannedTxn(amount,payee,dateDue,category.categoryId)
            var actual:ActualTxn? = null
            if(datePaid != null) {
                val amountPaid = binding.actualAmount?.text.toString()
                if(amountPaid.isNotEmpty()) {
                    actual = ActualTxn(-1L,datePaid,amountPaid.toFloat())
                }

            }

            var noteToSave: PlanNote? = null
            //did the user add a note?
            if(binding.itemAddenda?.text!=null) {
                val note = binding.itemAddenda?.text!!.trim().toString()
                if(note.isNotEmpty())
                    noteToSave = PlanNote(-1L,note)
            }
            /*
            since we don't yet have a real txnId value to plug into actual and noteToSave
            (one or both possibly being null), we'll send what we have to the viewModel to straighten things out
            */
            model.insert(txn,category,noteToSave,actual)

            model.message.observe(viewLifecycleOwner) { result ->
                if(result =="OK"){


                    binding.root.let { v ->
                        Snackbar.make(v, "Successfully saved", Snackbar.LENGTH_SHORT)
                            .addCallback(Bailor())
                            .show()
                    }
                }
            }
            return
        }
        (item as TxnWithCategory).let {
            it.amount = binding.plannedAmount?.text.toString().toFloat()
            val typedPayee = binding.payee?.text!!.trim().toString()
            if(typedPayee.isNotEmpty()) {
                it.payee = typedPayee
            }
            var noteToSave: PlanNote? = null
            //did the user add a note?
            if(binding.itemAddenda?.text!=null) {
                val note = binding.itemAddenda?.text!!.trim().toString()
                if(note.isNotEmpty())
                    noteToSave = PlanNote(it.txn.txnId,note)
            }
            val datePaid = getCalendarForDate(binding.actualDate?.text.toString())
            if(datePaid != null) {
                val amountPaid = binding.actualAmount?.text.toString()
                if(amountPaid.isNotEmpty()) {
                    model.update(it,ActualTxn(it.txn.txnId,datePaid,amountPaid.toFloat()),noteToSave)
                }
            } else {
                model.update(it,note = noteToSave)
            }

            //findNavController().navigateUp()
            model.message.observe(viewLifecycleOwner) { result ->
                if(result =="OK"){


                    binding.root.let { v ->
                        Snackbar.make(v, "Successfully saved", Snackbar.LENGTH_SHORT)
                            .addCallback(Bailor())
                            .show()
                    }
                }
            }
        }
    }


    private fun updateContent() {
        binding.txnTitle?.text = item?.transactionTitle
        (item as TxnWithCategory?)?.let {
            // itemDetailTextView.text = it.getNotes() TODO
            //   val fullAmt = it.amount
            val toShow = it.amount.toString()
            val remainingAmt = getOutstandingAmount(it)
            if(remainingAmt <=0f) {
                //toShow = getTotalPaid().toString()
                binding.plannedDate?.text = getString(R.string.lbl_paid)
                binding.plannedDate?.isEnabled = false
                if(!it.category?.openEnded!!) {
                    binding.actualDate?.text = getString(R.string.lbl_paid)
                    binding.actualDate?.isEnabled = false
                }

            } else {

                if(it.dateDue().trim().isNotEmpty()) {
                    binding.plannedDate?.text = it.dateDue()
                }
                binding.plannedDate?.isEnabled = true
            }

            binding.plannedAmount?.setText(toShow)
            binding.payee?.setText(it.payee)

            (binding.categoryMenu!!.editText as? AutoCompleteTextView)?.setText(it.category?.name,false)

            var month = getCalendarForDate(binding.plannedDate!!.text)
            if(month == null)
            {
                month = getCalendarForDate(binding.actualDate!!.text)
            }
            model.getActualTxnsForCategory(it.categoryId,month).observe(viewLifecycleOwner,{
                    actualsList -> actualTxns = actualsList
                updateContent()
            })
        }

        val totalPaid = getTotalPaid()
        if(totalPaid == 0f) {
            binding.paidAmount?.text = ""
        }
        else {
            binding.paidAmount?.text = getString(R.string.paid_so_far, getTotalPaid())
        }
        var toto=notes
        if(actualTxns != null) {
            for(line in actualTxns!!) {
                if(toto.isNotEmpty()) {
                    toto+="\r\n"
                }
                toto+="${line.datePaid()}:${line.amount} paid"
            }
        }

        binding.itemNotes?.text = toto
    }

    private fun getTotalPaid():Float {
        var amt = 0f
        if(actualTxns != null) {
            for(tx in actualTxns!!) {
                amt += tx.amount
            }
        }
        return amt
    }
    private fun getOutstandingAmount(it: TxnWithCategory): Float {
        //amt -=getTotalPaid()
        return it.getOutstandingAmount()
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