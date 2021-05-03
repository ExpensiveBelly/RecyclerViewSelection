package com.example.recyclerviewselection

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.selection.Selection
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox

class ItemsAdapter(
    recyclerView: () -> RecyclerView,
    itemClickListener: (Item) -> Unit,
    onSelectionChangedListener: SelectionTracker<String>.(Selection<String>) -> Unit
) :
    SelectionAdapter<Item, ItemsAdapter.ViewHolder>(
        recyclerView = recyclerView,
        itemLayout = R.layout.item_layout,
        viewHolderFactory = { ViewHolder(it, itemClickListener) },
        onSelectionChangedListener = onSelectionChangedListener
    ) {

    class ViewHolder(itemView: View, private val itemClickListener: (Item) -> Unit) :
        SelectionViewHolder<Item>(itemView) {
        private val name = itemView.findViewById<TextView>(R.id.name)
        private val details = itemView.findViewById<TextView>(R.id.details)
        private val image = itemView.findViewById<ImageButton>(R.id.image)
        override val selectionContainer = itemView.findViewById<View>(R.id.container)

        override fun bind(item: Item, isActivated: Boolean) {
            super.bind(item, isActivated)
            details.isVisible = !isActivated
            selectionContainer.alpha = if (isActivated) 0.5f else 1.0f
        }

        override fun bind(item: Item) {
            selectionContainer.setOnClickListener { itemClickListener(item) }
            name.text = item.name
            details.text = item.details
        }

        override val viewInSelectionHotSpot = { image }
    }
}