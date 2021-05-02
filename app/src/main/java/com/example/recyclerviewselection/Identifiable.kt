package com.example.recyclerviewselection

import java.io.Serializable

interface Identifiable : Serializable {
    val key: String
}