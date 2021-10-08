package com.manabie.todo.utils

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.manabie.todo.data.source.TasksRepository
import com.manabie.todo.ui.completed.CompletedTaskViewModel
import com.manabie.todo.ui.newtask.NewTaskViewModel
import com.manabie.todo.ui.task.TaskViewModel

class ViewModelFactory(
    private val tasksRepository: TasksRepository,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    override fun <T : ViewModel?> create(
        key: String, modelClass: Class<T>, handle: SavedStateHandle
    ) = with(modelClass) {
        when {
            isAssignableFrom(TaskViewModel::class.java) -> TaskViewModel(tasksRepository)
            isAssignableFrom(CompletedTaskViewModel::class.java) -> CompletedTaskViewModel(
                tasksRepository
            )
            isAssignableFrom(NewTaskViewModel::class.java) -> NewTaskViewModel(tasksRepository)
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    } as T
}