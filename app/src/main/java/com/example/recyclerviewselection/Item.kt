package com.example.recyclerviewselection

data class Item(
    override val key: String,
    val name: String,
    val details: String,
    val deprecated: Boolean = false
) : Identifiable