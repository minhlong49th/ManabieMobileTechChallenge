package com.manabie.todo.ui.task

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.manabie.todo.data.entities.Task

@BindingAdapter("app:items")
fun setItems(listView: RecyclerView, items: List<Task>?) {
    items?.let {
        (listView.adapter as TasksAdapter).submitList(items)
    }
}