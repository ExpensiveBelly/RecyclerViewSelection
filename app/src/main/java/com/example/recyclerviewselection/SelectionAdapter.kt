package com.example.recyclerviewselection

import android.view.LayoutInflater
import android.view.MotionEvent
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
import com.cmcmarkets.util.selection.ISelectionContainer
import com.cmcmarkets.util.selection.ISelectionItemDetails

typealias ViewType = Int
typealias LayoutResource = Int

abstract class SelectionAdapter<T : Identifiable, VH : SelectionViewHolder<T>>(
    recyclerView: () -> RecyclerView,
    @LayoutRes private val itemLayout: LayoutResource,
    private val viewHolderFactory: (view: View, viewType: ViewType) -> VH,
    diffCallback: DiffUtil.ItemCallback<T> = EqualsDiffItemCallback(),
    onSelectionChangedListener: SelectionTracker<String>.(Selection<String>) -> Unit
) : ListAdapter<T, VH>(diffCallback) {

    private val tracker: SelectionTracker<String> by lazy {
        createMultipleItemsSelectionTracker(
            recyclerView = recyclerView(),
            keyProvider = { pos -> getItemKey(pos) },
            positionProvider = { key -> getItemPosition(key) },
            onSelectionChangedListener = onSelectionChangedListener,
            selectionPredicate = selectionPredicate
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        viewHolderFactory(
            LayoutInflater.from(parent.context).inflate(itemLayout, parent, false),
            viewType
        )

    abstract val selectionPredicate: SelectionTracker.SelectionPredicate<String>

    abstract val inSelectionHotSpot: (viewType: Int, itemView: View, event: MotionEvent) -> Boolean

    private fun getItemKey(position: Int) = getItemId(currentList[position])

    private fun getItemPosition(key: String) = currentList.indexOfFirst { getItemId(it) == key }

    private fun getItemId(item: T) = item.key

    override fun onBindViewHolder(holder: VH, position: Int) {
        val isActivated = tracker.isSelected(getItemKey(position))
        val item = getItem(position)
        holder.bind(item, isActivated, tracker.hasSelection())
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

abstract class SelectionViewHolder<T : Identifiable>(
    itemView: View,
    private val inSelectionHotSpot: (event: MotionEvent) -> Boolean
) :
    RecyclerView.ViewHolder(itemView), ISelectionItemDetails, ISelectionContainer {

    private var key: String? = null

    override fun getItemDetails(): ItemDetailsLookup.ItemDetails<String> =
        createItemDefaultItemDetails(
            adapterPosition = adapterPosition,
            selectionKey = key,
            inSelectionHotSpot = inSelectionHotSpot
        )

    abstract fun bind(item: T)

    open fun bind(item: T, isActivated: Boolean, isActionModeEnabled: Boolean) {
        selectionContainer.isActivated = isActivated
        key = item.key
        bind(item)
    }

    @CallSuper
    open fun unbind() {
        key = null
    }
}