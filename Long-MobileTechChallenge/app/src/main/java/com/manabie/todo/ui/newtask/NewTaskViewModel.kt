package com.manabie.todo.ui.newtask

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manabie.todo.R
import com.manabie.todo.data.entities.Task
import com.manabie.todo.data.source.TasksRepository
import com.manabie.todo.data.succeeded
import com.manabie.todo.utils.Event
import kotlinx.coroutines.launch

class NewTaskViewModel(
    private val tasksRepository: TasksRepository,
) : ViewModel() {

    val title = MutableLiveData<String>()
    val description = MutableLiveData<String>()

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _taskSavedEvent = MutableLiveData<Event<Unit>>()
    val taskSavedEvent: LiveData<Event<Unit>> = _taskSavedEvent

    fun saveTask() {
        val currentTitle = title.value
        val currentDescription = description.value

        if (currentTitle.isNullOrEmpty() || currentDescription.isNullOrEmpty()) {
            _snackbarText.value = Event(R.string.empty_task_message)
            return
        }

        createTask(currentTitle, currentDescription)
    }

    private fun createTask(currentTitle: String, currentDescription: String) =
        viewModelScope.launch {
            val result = tasksRepository.saveTask(Task(currentTitle, currentDescription))
            if (result.succeeded) {
                _taskSavedEvent.value = Event(Unit)
            } else {
                _snackbarText.value = Event(R.string.create_new_task_message)
            }
        }

}