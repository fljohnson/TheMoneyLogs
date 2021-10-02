package com.fouracessoftware.themoneylogs

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipDescription
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.fouracessoftware.themoneylogs.data.PrototypeContent
import com.fouracessoftware.themoneylogs.data.roomy.Category
import com.fouracessoftware.themoneylogs.data.roomy.PlannedTxn
import com.fouracessoftware.themoneylogs.data.roomy.TxnWithCategory
import com.fouracessoftware.themoneylogs.databinding.FragmentItemListBinding
import com.fouracessoftware.themoneylogs.databinding.ItemListContentBinding
import java.util.*
import kotlin.collections.ArrayList

/**
 * A Fragment representing a list of Pings. This fragment
 * has different presentations for handset and larger screen devices. On
 * handsets, the fragment presents a list of items, which when touched,
 * lead to a {@link ItemDetailFragment} representing
 * item details. On larger screens, the Navigation controller presents the list of items and
 * item details side-by-side using two vertical panes.
 */

class ItemListFragment : Fragment(), Observer<ArrayList<TxnWithCategory>> {
    private var txns_ready: Boolean = false
    private var categories_ready: Boolean = false
    private var just_started: Boolean = true

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
            true
        } else if (event.keyCode == KeyEvent.KEYCODE_F && event.isCtrlPressed) {
            Toast.makeText(
                    v.context,
                    "Find (Ctrl + F) shortcut triggered",
                    Toast.LENGTH_LONG
            ).show()
            true
        }
        false
    }

    private val model: ItemViewModel by activityViewModels()

    private var _binding: FragmentItemListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        /*
        model.loadCategories()
        model.categoryList.observe(viewLifecycleOwner,CategoryListHelper(this))
        model.loadTxns()
        model.txnList.observe(viewLifecycleOwner,TxnListHelper(this))
        */
        model.txnExtraList.observe(viewLifecycleOwner,this)
        _binding = FragmentItemListBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.addOnUnhandledKeyEventListener(view, unhandledKeyEventListenerCompat)

        val recyclerView: RecyclerView = binding.itemList

        // Leaving this not using view binding as it relies on if the view is visible the current
        // layout configuration (layout, layout-sw600dp)
        val itemDetailFragmentContainer: View? = view.findViewById(R.id.item_detail_nav_container)

        /** Click Listener to trigger navigation based on if you have
         * a single pane layout or two pane layout
         */
        val onClickListener = View.OnClickListener { itemView ->
            val item = itemView.tag as TxnWithCategory
            model.select(item)
            val bundle = Bundle()
            /*
            bundle.putInt(
                    ItemDetailFragment.ARG_ITEM_ID,
                    item.id
            )*/
            if (itemDetailFragmentContainer != null) {
                itemDetailFragmentContainer.findNavController()
                        .navigate(R.id.fragment_item_detail, bundle)
            } else {
                itemView.findNavController().navigate(R.id.show_item_detail, bundle)
            }
        }

        /**
         * Context click listener to handle Right click events
         * from mice and trackpad input to provide a more native
         * experience on larger screen devices
         */
        val onContextClickListener = View.OnContextClickListener { v ->
            val item = v.tag as PrototypeContent.PrototypeItem
            Toast.makeText(
                    v.context,
                    "Context click of item " + item.id,
                    Toast.LENGTH_LONG
            ).show()
            true
        }
       // setupRecyclerView(recyclerView, onClickListener, onContextClickListener)
    }

    private fun setupRecyclerView(
            recyclerView: RecyclerView,
            onClickListener: View.OnClickListener,
            onContextClickListener: View.OnContextClickListener
    ) {

        recyclerView.adapter = SimpleItemRecyclerViewAdapter(
            model.txnExtraList.value!!,
            model.categoryList.value!!,
            onClickListener,
            onContextClickListener
        )
    }

    class SimpleItemRecyclerViewAdapter(
        private val values: ArrayList<TxnWithCategory>,
        private val categories: List<Category>,
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
            var categoryName = item.category.name //"[Missing]"
            /*
            for(c in categories){
                if(item.categoryId==c.categoryId) {
                    categoryName = c.name
                    break
                }
            }*/

            if(item.amount() <0.01f) {
                (item.amount().toString() + " for " + categoryName +  " PAID").also { holder.idView.text = it }
            }
            else {

                (item.amount().toString() + " for " + categoryName +  " on " + item.dateDue()).also {
                    holder.idView.text = it
                }
            }
            item.payee().also { holder.contentView.text = it }

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

    fun showList() {

       // (binding.itemList.adapter as SimpleItemRecyclerViewAdapter).da
        val onClickListener = View.OnClickListener { itemView ->
            val item = itemView.tag as TxnWithCategory
            model.select(item)
            val bundle = Bundle()
            /*
            bundle.putInt(
                    ItemDetailFragment.ARG_ITEM_ID,
                    item.id
            )*/
            /*
            if (itemDetailFragmentContainer != null) {
                itemDetailFragmentContainer.findNavController()
                    .navigate(R.id.fragment_item_detail, bundle)
            } else {
                itemView.findNavController().navigate(R.id.show_item_detail, bundle)
            }*/
        }
        val onContextClickListener = View.OnContextClickListener{false}

        setupRecyclerView(binding.itemList,onClickListener,onContextClickListener)
    }


    override fun onChanged(t: ArrayList<TxnWithCategory>?) {
        showList()
    }

    fun armCategories() {
        categories_ready = true;
        if(txns_ready && categories_ready) {
            showList()
        }
    }


    fun armTxns() {
        txns_ready = true;
        if(txns_ready && categories_ready) {
            showList()
       }
    }

    private class CategoryListHelper(val caller:ItemListFragment) : Observer<List<Category>> {
        override fun onChanged(t: List<Category>?) {
            caller.armCategories()
        }

    }

    private class TxnListHelper(val caller:ItemListFragment) : Observer<List<PlannedTxn>> {
        override fun onChanged(t: List<PlannedTxn>?) {
            caller.armTxns()
        }
    }

}