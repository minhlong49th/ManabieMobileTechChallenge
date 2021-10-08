package com.manabie.todo.ui.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manabie.todo.R
import com.manabie.todo.data.Result
import com.manabie.todo.data.entities.Task
import com.manabie.todo.data.source.TasksRepository
import com.manabie.todo.utils.Event
import kotlinx.coroutines.launch

class TaskViewModel(
    private val tasksRepository: TasksRepository
) : ViewModel() {

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _items = MutableLiveData<List<Task>>()
    val items: LiveData<List<Task>> = _items

    private val _item = MutableLiveData<Event<Task>>()
    val item: LiveData<Event<Task>> = _item

    init {
        getTasks()
    }

    fun completeTask(task: Task) = viewModelScope.launch {
        try {
            tasksRepository.completeTask(task)
            showSnackbarMessage(R.string.task_marked_complete)
            getTasks()
        } catch (e: Exception) {
            showSnackbarMessage(R.string.update_complete_error_message)
        }
    }

    fun openTask(taskId: String) = viewModelScope.launch {
        try {
            val task = tasksRepository.getTask(taskId)
            if (task is Result.Success) {
                _item.value = Event(task.data)
            } else {
                showSnackbarMessage(R.string.not_found_task_error_message)
            }
        } catch (e: Exception) {
            showSnackbarMessage(R.string.not_found_task_error_message)
        }
    }

    fun getTasks() = viewModelScope.launch {
        val result = tasksRepository.getTasks(false)
        if (result is Result.Success) {
            _items.value = result.data ?: emptyList()
        } else {
            _items.value = emptyList()
        }
    }

    private fun showSnackbarMessage(message: Int) {
        _snackbarText.value = Event(message)
    }
}