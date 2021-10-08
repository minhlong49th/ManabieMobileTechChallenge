package com.manabie.todo.data.source

import com.manabie.todo.data.Result
import com.manabie.todo.data.entities.Task

interface TasksDataSource {
    suspend fun getTasks(isCompleted: Boolean): Result<List<Task>>
    suspend fun saveTask(task: Task): Result<Boolean>
    suspend fun getTask(taskId: String): Result<Task>
    suspend fun completeTask(taskId: String)
    suspend fun activateTask(taskId: String)
}