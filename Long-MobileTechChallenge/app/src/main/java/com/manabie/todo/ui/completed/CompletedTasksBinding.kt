package com.manabie.todo.ui.completed

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.manabie.todo.data.entities.Task

@BindingAdapter("app:completed_items")
fun setItems(listView: RecyclerView, items: List<Task>?) {
    items?.let {
        (listView.adapter as CompletedTasksAdapter).submitList(items)
    }
}