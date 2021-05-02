package com.cmcmarkets.util.selection

import androidx.recyclerview.selection.ItemDetailsLookup

interface ISelectionItemDetails {
    fun getItemDetails(): ItemDetailsLookup.ItemDetails<String>
}