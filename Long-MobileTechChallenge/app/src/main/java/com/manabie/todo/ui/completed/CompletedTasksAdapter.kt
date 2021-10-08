package com.manabie.todo.ui.completed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.manabie.todo.data.entities.Task
import com.manabie.todo.databinding.CompletedTaskItemBinding

class CompletedTasksAdapter(private val viewModel: CompletedTaskViewModel) :
    ListAdapter<Task, CompletedTasksAdapter.ViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(viewModel, item)
    }

    class ViewHolder private constructor(private val binding: CompletedTaskItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: CompletedTaskViewModel, item: Task) {

            binding.viewmodel = viewModel
            binding.task = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = CompletedTaskItemBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }

}

class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem == newItem
    }

}