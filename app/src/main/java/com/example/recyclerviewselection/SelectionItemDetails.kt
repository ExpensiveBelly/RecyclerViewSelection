package com.cmcmarkets.util.selection

import androidx.recyclerview.selection.ItemDetailsLookup

interface SelectionItemDetails {
    fun getItemDetails(): ItemDetailsLookup.ItemDetails<String>
}