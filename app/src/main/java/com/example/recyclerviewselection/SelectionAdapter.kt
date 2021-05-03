package com.example.recyclerviewselection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.Selection
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cmcmarkets.util.selection.SelectionContainer
import com.cmcmarkets.util.selection.SelectionItemDetails

typealias LayoutResource = Int

abstract class SelectionAdapter<T : Identifiable, VH : SelectionViewHolder<T>>(
    recyclerView: () -> RecyclerView,
    @LayoutRes private val itemLayout: LayoutResource,
    private val viewHolderFactory: (view: View) -> VH,
    diffCallback: DiffUtil.ItemCallback<T> = EqualsDiffItemCallback(),
    onSelectionChangedListener: SelectionTracker<String>.(Selection<String>) -> Unit
) : ListAdapter<T, VH>(diffCallback) {

    private val tracker: SelectionTracker<String> by lazy {
        createMultipleItemsSelectionTracker(
            recyclerView = recyclerView(),
            keyProvider = { pos -> getItemKey(pos) },
            positionProvider = { key -> getItemPosition(key) },
            onSelectionChangedListener = onSelectionChangedListener
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        viewHolderFactory(LayoutInflater.from(parent.context).inflate(itemLayout, parent, false))

    private fun getItemKey(position: Int) =
        if (position in 0 until currentList.size) getItemId(currentList[position]) else null

    private fun getItemPosition(key: String) = currentList.indexOfFirst { getItemId(it) == key }

    private fun getItemId(item: T) = item.key

    override fun onBindViewHolder(holder: VH, position: Int) {
        val isActivated = tracker.isSelected(getItemKey(position))
        val item = getItem(position)
        holder.bind(item, isActivated)
    }

    override fun onViewRecycled(holder: VH) {
        holder.unbind()
        super.onViewRecycled(holder)
    }

    override fun onFailedToRecycleView(holder: VH): Boolean {
        holder.unbind()
        return super.onFailedToRecycleView(holder)
    }
}

abstract class SelectionViewHolder<T : Identifiable>(itemView: View) :
    RecyclerView.ViewHolder(itemView), SelectionItemDetails, SelectionContainer {

    private var key: String? = null

    abstract val viewInSelectionHotSpot: () -> View

    override fun getItemDetails(): ItemDetailsLookup.ItemDetails<String> =
        createItemDefaultItemDetails(
            adapterPosition = adapterPosition,
            selectionKey = key,
            viewInSelectionHotSpot = viewInSelectionHotSpot
        )

    abstract fun bind(item: T)

    open fun bind(item: T, isActivated: Boolean) {
        selectionContainer.isActivated = isActivated
        key = item.key
        bind(item)
    }

    @CallSuper
    open fun unbind() {
        key = null
    }
}