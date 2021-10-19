package com.fouracessoftware.themoneylogs

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipDescription
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.fouracessoftware.themoneylogs.data.PrototypeContent
import com.fouracessoftware.themoneylogs.data.roomy.TxnWithCategory
import com.fouracessoftware.themoneylogs.databinding.FragmentItemListBinding
import com.fouracessoftware.themoneylogs.databinding.ItemListContentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import java.util.*

/**
 * A Fragment representing a list of Pings. This fragment
 * has different presentations for handset and larger screen devices. On
 * handsets, the fragment presents a list of items, which when touched,
 * lead to a {@link ItemDetailFragment} representing
 * item details. On larger screens, the Navigation controller presents the list of items and
 * item details side-by-side using two vertical panes.
 */

class ItemListFragment : Fragment(), Observer<List<TxnWithCategory>> {
    private lateinit var storedItemOnClickListener: View.OnClickListener
    private lateinit var storedOnContextClickListener:View.OnContextClickListener

    /**
     * Method to intercept global key events in the
     * item list fragment to trigger keyboard shortcuts
     * Currently provides a toast when Ctrl + Z and Ctrl + F
     * are triggered
     */
    private val unhandledKeyEventListenerCompat = ViewCompat.OnUnhandledKeyEventListenerCompat { v, event ->
        if (event.keyCode == KeyEvent.KEYCODE_Z && event.isCtrlPressed) {
            Toast.makeText(
                    v.context,
                    "Undo (Ctrl + Z) shortcut triggered",
                    Toast.LENGTH_LONG
            ).show()
            //true
        } else if (event.keyCode == KeyEvent.KEYCODE_F && event.isCtrlPressed) {
            Toast.makeText(
                    v.context,
                    "Find (Ctrl + F) shortcut triggered",
                    Toast.LENGTH_LONG
            ).show()
            //true
        }
        false
    }

    private val model: TxnListViewModel by viewModels()

    private var _binding: FragmentItemListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        model.txnList.observe(viewLifecycleOwner,this)
        _binding = FragmentItemListBinding.inflate(inflater, container, false)


        binding.toolbar?.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.action_copy -> {
                    startCopyDlg()
                    true
                }
                R.id.action_monthfilter -> {
                    startMonthfilterDlg()
                    true
                }
                R.id.action_currentmonth -> {
                    clearMonthFilter()
                    true
                }
                else -> {
                    false
                }
            }
        }

        return binding.root

    }

    private fun startCopyDlg() {
        //val v = layoutInflater.inflate(R.layout.month_range_dlg,this.requireParentFragment())
        //val fromMenu = v.findViewById<MaterialAutoCompleteTextView>(R.id.from_list)
        val fromMenu:AutoCompleteTextView?
        val toMenu: AutoCompleteTextView?
        val moFo = SimpleDateFormat("LLL yyyy", Locale.getDefault())
        val priorMonthsVisual = arrayOf("","","")
        val priorMonthsMath:Array<Calendar?> = Array(3) {null}
        val targetMonthsVisual = arrayOf("","","")
        val targetMonthsMath:Array<Calendar?> = Array(3) {null}
        var fromMo = 2
        var toMo = 0
        for(i in (1..3)) {
            val cal = Calendar.getInstance(TimeZone.GMT_ZONE) //now, in UTC terms
            cal.add(Calendar.MONTH,-i)
            priorMonthsMath[3-i]=cal
            priorMonthsVisual[3-i]=moFo.format(cal)
        }
        for(i in (0..2)){
            val cal = Calendar.getInstance(TimeZone.GMT_ZONE) //now, in UTC terms
            cal.add(Calendar.MONTH,+i)
            targetMonthsMath[i] = cal
            targetMonthsVisual[i] = moFo.format(cal)

        }
        val mess = MaterialAlertDialogBuilder(requireContext()).setView(R.layout.month_range_dlg)
            .setNeutralButton(resources.getString(R.string.lbl_cancel)) { _, _ ->
                println("Nothing chosen")
            }
            .setPositiveButton(resources.getString(R.string.lbl_ok)) { _, _ ->

                if(fromMo !=-1 && toMo !=-1) {
                    println("From:"+moFo.format(priorMonthsMath[fromMo]))
                    println("To:"+moFo.format(targetMonthsMath[toMo]))
                }
            }
            .show()

        fromMenu = mess.findViewById<TextInputLayout>(R.id.from_list)?.editText as? AutoCompleteTextView
        toMenu = mess.findViewById<TextInputLayout>(R.id.to_list)?.editText as? AutoCompleteTextView

        fromMenu?.setAdapter(ArrayAdapter(requireContext(),android.R.layout.simple_dropdown_item_1line, priorMonthsVisual))
        fromMenu?.setText(priorMonthsVisual[2],false)
        fromMenu?.setOnItemClickListener { _: AdapterView<*>, _: View, i: Int, _: Long ->
            fromMo = i
        }
        toMenu?.setAdapter(ArrayAdapter(requireContext(),android.R.layout.simple_dropdown_item_1line, targetMonthsVisual))
        toMenu?.setText(targetMonthsVisual[0],false)
        toMenu?.setOnItemClickListener { _: AdapterView<*>, _: View, i: Int, _: Long ->
            toMo = i
        }
    }

    private fun startMonthfilterDlg() {
        val moFo = SimpleDateFormat("LLL yyyy", Locale.getDefault())
        val visuals = arrayOf("","","","","","")
        var choice = 2
        val datelets :Array<Calendar?> = Array(6) { null }
        for(i in (1..3)) {
            val cal = Calendar.getInstance(TimeZone.GMT_ZONE) //now, in UTC terms
            cal.add(Calendar.MONTH,-i)
            datelets[3-i]=cal
        }
        for(i in (1..3)) {
            val cal = Calendar.getInstance(TimeZone.GMT_ZONE) //now, in UTC terms
            cal.add(Calendar.MONTH,+i)
            datelets[2+i]=cal
        }

        for(i in 0..5) {
            visuals[i] =(moFo.format(datelets[i]))
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.lbl_choose_month))
            .setNeutralButton(resources.getString(R.string.lbl_cancel)) { _, _ ->
                choice = -1
            }
            .setPositiveButton(resources.getString(R.string.lbl_ok)) { _, _ ->
                setViewedMonth(datelets[choice])
            }
            // Single-choice items (initialized with checked item)
            .setSingleChoiceItems(visuals,choice) { _, which ->
                // Respond to item chosen
                choice = which
            }
            .show()


    }

    private fun setViewedMonth(calendar: Calendar?) {

        /*Okay, the user went through with a "view other month", so
                Hide the menuitem
                Show the "current month" item
                 */

        binding.toolbar?.menu?.findItem(R.id.action_monthfilter)?.isVisible = false
        binding.toolbar?.menu?.findItem(R.id.action_currentmonth)?.isVisible = true
        //now get the chosen month's data and make ready to show it
        model.getTxns(calendar)
        model.txnList.observe(viewLifecycleOwner,this)
    }

    private fun clearMonthFilter() {
        model.getTxns(null)
        model.txnList.observe(viewLifecycleOwner,this)
        //no bones to be made here

        binding.toolbar?.menu?.findItem(R.id.action_monthfilter)?.isVisible = true
        binding.toolbar?.menu?.findItem(R.id.action_currentmonth)?.isVisible = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        ViewCompat.addOnUnhandledKeyEventListener(view, unhandledKeyEventListenerCompat)



        // Leaving this not using view binding as it relies on if the view is visible the current
        // layout configuration (layout, layout-sw600dp)
        val itemDetailFragmentContainer: View? = view.findViewById(R.id.item_detail_nav_container)

        /** Click Listener to trigger navigation based on if you have
         * a single pane layout or two pane layout
         */
        storedItemOnClickListener = View.OnClickListener { itemView ->
            val item = itemView.tag as TxnWithCategory

            openDetail(itemDetailFragmentContainer,item.txn.txnId,itemView)

        }



        /**
         * Context click listener to handle Right click events
         * from mice and trackpad input to provide a more native
         * experience on larger screen devices
         */
        storedOnContextClickListener = View.OnContextClickListener { v ->
            val item = v.tag as PrototypeContent.PrototypeItem
            Toast.makeText(
                    v.context,
                    "Context click of item " + item.id,
                    Toast.LENGTH_LONG
            ).show()
            true
        }

        binding.btnNewPlanned?.setOnClickListener{
            openDetail(itemDetailFragmentContainer,-1L,it)
        }
    }

    private fun openDetail(itemDetailFragmentContainer:View?,txnId: Long, itemView: View) {
        val bundle = Bundle()
        bundle.putLong(
            ItemDetailFragment.ARG_ITEM_ID,
            txnId
        )

      //  val itemDetailFragmentContainer: View? = view.findViewById(R.id.item_detail_nav_container)

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        if (itemDetailFragmentContainer != null) {
            itemDetailFragmentContainer.findNavController()
                .navigate(R.id.fragment_item_detail, bundle)
        } else {
            itemView.findNavController().navigate(R.id.show_item_detail, bundle)
        }
    }

    private fun setupRecyclerView(
            recyclerView: RecyclerView,
            onClickListener: View.OnClickListener,
            onContextClickListener: View.OnContextClickListener
    ) {

        recyclerView.adapter = SimpleItemRecyclerViewAdapter(
            model.txnList.value!!,
            onClickListener,
            onContextClickListener
        )
    }

    class SimpleItemRecyclerViewAdapter(
        private val values: List<TxnWithCategory>,
        private val onClickListener: View.OnClickListener,
        private val onContextClickListener: View.OnContextClickListener
    ) :
            RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            val binding = ItemListContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)

        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = values[position]
            val categoryName = item.category!!.name

            if(item.getOutstandingAmount() <0.01f) {
                (item.amount.toString() + " for " + categoryName +  " PAID").also { holder.idView.text = it }
            }
            else {

                (item.amount.toString() + " for " + categoryName +  " on " + item.dateDue()).also {
                    holder.idView.text = it
                }
            }
            item.payee.also { holder.contentView.text = it }

            with(holder.itemView) {
                tag = item
                setOnClickListener(onClickListener)
                setOnContextClickListener(onContextClickListener)

                setOnLongClickListener { v ->
                    // Setting the item id as the clip data so that the drop target is able to
                    // identify the id of the content
                    val clipItem = ClipData.Item(item.txn.txnId.toString())
                    val dragData = ClipData(
                            v.tag as? CharSequence,
                            arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                            clipItem
                    )

                    v.startDragAndDrop(
                            dragData,
                            View.DragShadowBuilder(v),
                            null,
                            0
                    )
                }
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(binding: ItemListContentBinding) : RecyclerView.ViewHolder(binding.root) {
            val idView: TextView = binding.idText
            val contentView: TextView = binding.content
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showList() {
        setupRecyclerView(binding.itemList,storedItemOnClickListener,storedOnContextClickListener)
    }


    override fun onChanged(t: List<TxnWithCategory>?) {
        showList()
    }

}