package com.example.recyclerviewselection

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

class EqualsDiffItemCallback<T> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(item1: T, item2: T) = areContentsTheSame(item1, item2)

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(item1: T, item2: T) = item1 == item2
}