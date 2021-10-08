package com.manabie.todo.data.source.local

import com.manabie.todo.data.Result
import com.manabie.todo.data.Result.Error
import com.manabie.todo.data.Result.Success
import com.manabie.todo.data.entities.Task
import com.manabie.todo.data.source.TasksDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TasksLocalDataSource(
    private val tasksDao: TasksDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TasksDataSource {

    override suspend fun getTasks(isCompleted: Boolean): Result<List<Task>> =
        withContext(ioDispatcher) {
            return@withContext try {
                Success(tasksDao.getTasks(isCompleted))
            } catch (e: Exception) {
                Error(e)
            }
        }

    override suspend fun saveTask(task: Task): Result<Boolean> = withContext(ioDispatcher) {
        return@withContext if (tasksDao.insertTask(task) > -1) {
            Success(true)
        } else {
            Error(Exception())
        }
    }

    override suspend fun getTask(taskId: String): Result<Task> = withContext(ioDispatcher) {
        return@withContext try {
            Success(tasksDao.getTaskById(taskId)!!)
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun completeTask(taskId: String) = withContext(ioDispatcher) {
        tasksDao.updateCompleted(taskId, true)
    }

    override suspend fun activateTask(taskId: String) = withContext(ioDispatcher) {
        tasksDao.updateCompleted(taskId, false)
    }
}