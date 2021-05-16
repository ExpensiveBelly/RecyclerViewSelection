package com.example.recyclerviewselection

import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.selection.Selection
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.github.fsbarata.functional.data.curry

private const val NON_DEPRECATED_VIEW_TYPE = 0
private const val DEPRECATED_VIEW_TYPE = 1

class ItemsAdapter(
    recyclerView: () -> RecyclerView,
    itemClickListener: (Item) -> Unit,
    onSelectionChangedListener: SelectionTracker<String>.(Selection<String>) -> Unit,
    inSelectionHotSpot: (viewType: Int, itemView: View, event: MotionEvent) -> Boolean = { viewType: Int, itemView: View, event: MotionEvent ->
        when (viewType) {
            NON_DEPRECATED_VIEW_TYPE -> itemView.findViewById<View>(R.id.image)
                .isEventWithinViewBounds(event)
            DEPRECATED_VIEW_TYPE -> false
            else -> throw NotImplementedError()
        }
    },
    private val enabledPredicate: (Item) -> Boolean = { !it.deprecated }
) : SelectionAdapter<Item, ItemsAdapter.ViewHolder>(
    recyclerView = recyclerView,
    itemLayout = R.layout.item_layout,
    viewHolderFactory = { view, viewType ->
        val inSelectionHotSpotCurried = inSelectionHotSpot.curry()(viewType)(view)
        when (viewType) {
            NON_DEPRECATED_VIEW_TYPE -> ViewHolder(
                view,
                itemClickListener,
                inSelectionHotSpotCurried,
                enabledPredicate,
                viewType
            )
            DEPRECATED_VIEW_TYPE -> ViewHolder(
                view,
                itemClickListener,
                inSelectionHotSpotCurried,
                enabledPredicate,
                viewType
            )
            else -> throw NotImplementedError()
        }
    },
    onSelectionChangedListener = onSelectionChangedListener
) {

    override fun getItemViewType(position: Int) = when (getItem(position).deprecated) {
        true -> DEPRECATED_VIEW_TYPE
        false -> NON_DEPRECATED_VIEW_TYPE
    }

    override val inSelectionHotSpot: (viewType: Int, itemView: View, event: MotionEvent) -> Boolean =
        { viewType: Int, itemView: View, event: MotionEvent ->
            when (viewType) {
                NON_DEPRECATED_VIEW_TYPE -> itemView.findViewById<View>(R.id.image)
                    .isEventWithinViewBounds(event)
                DEPRECATED_VIEW_TYPE -> false
                else -> throw NotImplementedError()
            }
        }

    fun notifyItemsWithPredicate() {
        val indexes =
            currentList.withIndex().mapNotNull { if (enabledPredicate(it.value)) null else it }
                .map { it.index }
        indexes.forEach { notifyItemChanged(it) }
    }

    class ViewHolder(
        itemView: View,
        private val itemClickListener: (Item) -> Unit,
        inSelectionHotSpot: (event: MotionEvent) -> Boolean,
        private val enabledPredicate: (Item) -> Boolean,
        private val viewType: ViewType
    ) : SelectionViewHolder<Item>(itemView, inSelectionHotSpot) {
        private val name = itemView.findViewById<TextView>(R.id.name)
        private val details = itemView.findViewById<TextView>(R.id.details)
        private val name_details_container =
            itemView.findViewById<View>(R.id.name_details_container)
        private val image = itemView.findViewById<ImageButton>(R.id.image)
        override val selectionContainer = itemView.findViewById<View>(R.id.container)

        override fun bind(item: Item, isActivated: Boolean, isActionModeEnabled: Boolean) {
            super.bind(item, isActivated, isActionModeEnabled)
            details.isVisible = !isActivated
            name_details_container.alpha = if (isActionModeEnabled && !enabledPredicate(item)) 0.2f else 1.0f
            if (viewType == DEPRECATED_VIEW_TYPE) image.setOnClickListener { itemView.context.toast("This item has been deprecated and can't be selected") }
        }

        override fun bind(item: Item) {
            selectionContainer.setOnClickListener { itemClickListener(item) }
            name.text = item.name
            details.text = item.details
        }
    }

    override val selectionPredicate: SelectionTracker.SelectionPredicate<String> =
        object : SelectionTracker.SelectionPredicate<String>() {
            override fun canSetStateForKey(key: String, nextState: Boolean) =
                if (nextState) currentList.find { it.key == key }?.let(enabledPredicate)
                    ?: false else true

            override fun canSetStateAtPosition(position: Int, nextState: Boolean) =
                if (nextState) currentList[position].let(enabledPredicate) else true

            override fun canSelectMultiple() = true
        }
}