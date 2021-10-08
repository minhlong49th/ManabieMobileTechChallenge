package com.manabie.todo.utils

import com.manabie.todo.data.entities.Task
import com.manabie.todo.data.source.TasksRepository
import kotlinx.coroutines.runBlocking

fun TasksRepository.saveTaskBlocking(task: Task) = runBlocking {
    this@saveTaskBlocking.saveTask(task)
}

fun TasksRepository.getTasksBlocking() = runBlocking {
    this@getTasksBlocking.getTasks(false)
}