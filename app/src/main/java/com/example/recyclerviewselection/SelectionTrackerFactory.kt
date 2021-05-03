package com.example.recyclerviewselection

import android.graphics.Rect
import android.view.*
import androidx.recyclerview.selection.*
import androidx.recyclerview.selection.SelectionPredicates.createSelectAnything
import androidx.recyclerview.selection.SelectionTracker.Builder
import androidx.recyclerview.widget.RecyclerView
import com.cmcmarkets.util.selection.SelectionItemDetails

private const val SELECTION_ID = "selection_id"

fun createMultipleItemsSelectionTracker(
    recyclerView: RecyclerView,
    keyProvider: (Int) -> String?,
    positionProvider: (String) -> Int,
    onSelectionChangedListener: SelectionTracker<String>.(Selection<String>) -> Unit
): SelectionTracker<String> =
    Builder(
        SELECTION_ID,
        recyclerView,
        RecyclerViewIdKeyProvider(keyProvider, positionProvider),
        AdapterItemDetailsLookup(recyclerView),
        StorageStrategy.createStringStorage(),
    ).withSelectionPredicate(createSelectAnything()).build().apply {
        addObserver(object : SelectionTracker.SelectionObserver<String>() {
            override fun onSelectionChanged() {
                onSelectionChangedListener(selection)
            }
        })
    }

/**
 * https://stackoverflow.com/questions/11354069/find-a-view-button-which-a-motion-event-ends-on
 */

internal fun createItemDefaultItemDetails(
    adapterPosition: Int,
    selectionKey: String?,
    viewInSelectionHotSpot: () -> View
) =
    object : ItemDetailsLookup.ItemDetails<String>() {
        override fun getPosition(): Int = adapterPosition
        override fun getSelectionKey(): String? = selectionKey
        override fun inSelectionHotspot(event: MotionEvent): Boolean {
            return isMotionEventInsideView(viewInSelectionHotSpot(), event)
        }

        private fun isMotionEventInsideView(view: View, event: MotionEvent): Boolean {
            val rect = Rect()
            view.getGlobalVisibleRect(rect)
            return rect.contains(
                event.rawX.toInt(),
                event.rawY.toInt()
            )
        }
    }


private class RecyclerViewIdKeyProvider(
    private val keyProvider: (Int) -> String?,
    private val positionProvider: (String) -> Int,
) : ItemKeyProvider<String>(SCOPE_CACHED) {
    override fun getKey(position: Int) = keyProvider(position)
    override fun getPosition(key: String) = positionProvider(key)
}

private class AdapterItemDetailsLookup(private val recyclerView: RecyclerView) :
    ItemDetailsLookup<String>() {
    override fun getItemDetails(event: MotionEvent) =
        recyclerView.findChildViewUnder(event.x, event.y)?.let { view ->
            (recyclerView.getChildViewHolder(view) as SelectionItemDetails).getItemDetails()
        }
}