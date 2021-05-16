package com.example.recyclerviewselection

import android.view.*
import androidx.recyclerview.selection.*
import androidx.recyclerview.selection.SelectionTracker.Builder
import androidx.recyclerview.widget.RecyclerView
import com.cmcmarkets.util.selection.ISelectionItemDetails

private const val SELECTION_ID = "selection_id"

fun createMultipleItemsSelectionTracker(
    recyclerView: RecyclerView,
    keyProvider: (Int) -> String?,
    positionProvider: (String) -> Int,
    onSelectionChangedListener: SelectionTracker<String>.(Selection<String>) -> Unit,
    selectionPredicate: SelectionTracker.SelectionPredicate<String>
): SelectionTracker<String> =
    Builder(
        SELECTION_ID,
        recyclerView,
        RecyclerViewIdKeyProvider(keyProvider, positionProvider),
        AdapterItemDetailsLookupSelectMultipleItems(recyclerView),
        StorageStrategy.createStringStorage(),
    ).withSelectionPredicate(selectionPredicate).build().apply {
        addObserver(object : SelectionTracker.SelectionObserver<String>() {
            override fun onSelectionChanged() {
                onSelectionChangedListener(selection)
            }
        })
    }

fun createItemDefaultItemDetails(
    adapterPosition: Int,
    selectionKey: String?,
    inSelectionHotSpot: (event: MotionEvent) -> Boolean
) =
    object : ItemDetailsLookup.ItemDetails<String>() {
        override fun getPosition(): Int = adapterPosition
        override fun getSelectionKey(): String? = selectionKey
        override fun inSelectionHotspot(event: MotionEvent) = inSelectionHotSpot(event)
    }

private class RecyclerViewIdKeyProvider(
    private val keyProvider: (Int) -> String?,
    private val positionProvider: (String) -> Int,
) : ItemKeyProvider<String>(SCOPE_CACHED) {
    override fun getKey(position: Int) = keyProvider(position)
    override fun getPosition(key: String) = positionProvider(key)
}

private class AdapterItemDetailsLookupSelectMultipleItems(private val recyclerView: RecyclerView) : ItemDetailsLookup<String>() {
    override fun getItemDetails(event: MotionEvent) =
        recyclerView.findChildViewUnder(event.x, event.y)?.let { view ->
            (recyclerView.getChildViewHolder(view) as ISelectionItemDetails).getItemDetails()
        }
}
