package com.manabie.todo.data.source

import com.manabie.todo.data.Result
import com.manabie.todo.data.entities.Task

class FakeDataSource(var tasks: MutableList<Task>? = mutableListOf()) : TasksDataSource {

    override suspend fun getTasks(isCompleted: Boolean): Result<List<Task>> {
        tasks?.filter { it.isCompleted == isCompleted }?.let { return Result.Success(it) }
        return Result.Error(Exception("Tasks not found"))
    }

    override suspend fun saveTask(task: Task): Result<Boolean> {
        tasks?.let {
            it.add(task)
            return Result.Success(true)
        }
        return Result.Error(Exception("Tasks not found"))
    }

    override suspend fun getTask(taskId: String): Result<Task> {
        tasks?.firstOrNull { it.id == taskId }?.let { return Result.Success(it) }
        return Result.Error(Exception("Task not found"))
    }

    override suspend fun completeTask(taskId: String) {
        tasks?.firstOrNull { it.id == taskId }?.apply { this.isCompleted = true }
    }

    override suspend fun activateTask(taskId: String) {
        tasks?.firstOrNull { it.id == taskId }?.apply { this.isCompleted = false }
    }
}