package com.manabie.todo.data.source

import androidx.annotation.VisibleForTesting
import com.manabie.todo.data.Result
import com.manabie.todo.data.entities.Task
import java.util.*

class FakeRepositoryForTest : TasksRepository {
    var tasksServiceData: LinkedHashMap<String, Task> = LinkedHashMap()

    private var shouldReturnError = false

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getTasks(isCompleted: Boolean): Result<List<Task>> {
        if (shouldReturnError) {
            return Result.Error(Exception("Test exception"))
        }
        return Result.Success(tasksServiceData.values.filter { it.isCompleted == isCompleted })
    }

    override suspend fun saveTask(task: Task): Result<Boolean> {
        tasksServiceData[task.id] = task
        return Result.Success(true)
    }

    override suspend fun getTask(taskId: String): Result<Task> {
        if (shouldReturnError) {
            return Result.Error(Exception("Test exception"))
        }
        tasksServiceData[taskId]?.let {
            return Result.Success(it)
        }
        return Result.Error(Exception("Could not find task"))
    }

    override suspend fun completeTask(task: Task) {
        val completedTask = Task(task.title, task.description, true, task.id)
        tasksServiceData[task.id] = completedTask
    }

    override suspend fun activateTask(task: Task) {
        val activeTask = Task(task.title, task.description, false, task.id)
        tasksServiceData[task.id] = activeTask
    }

    @VisibleForTesting
    fun addTasks(vararg tasks: Task) {
        for (task in tasks) {
            tasksServiceData[task.id] = task
        }
    }
}