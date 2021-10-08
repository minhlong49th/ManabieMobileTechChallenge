package com.manabie.todo.data.source

import com.manabie.todo.data.Result
import com.manabie.todo.data.entities.Task
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class DefaultTasksRepository(
    private val tasksLocalDataSource: TasksDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TasksRepository {

    override suspend fun getTasks(isCompleted: Boolean): Result<List<Task>> =
        tasksLocalDataSource.getTasks(isCompleted)

    override suspend fun saveTask(task: Task): Result<Boolean> = tasksLocalDataSource.saveTask(task)

    override suspend fun getTask(taskId: String): Result<Task> =
        tasksLocalDataSource.getTask(taskId)

    override suspend fun completeTask(task: Task) {
        coroutineScope {
            launch { tasksLocalDataSource.completeTask(task.id) }
        }
    }

    override suspend fun activateTask(task: Task) {
        coroutineScope {
            launch { tasksLocalDataSource.activateTask(task.id) }
        }
    }

}