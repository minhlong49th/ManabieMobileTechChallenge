package com.manabie.todo.utils

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.manabie.todo.data.source.DefaultTasksRepository
import com.manabie.todo.data.source.TasksDataSource
import com.manabie.todo.data.source.TasksRepository
import com.manabie.todo.data.source.local.TasksLocalDataSource
import com.manabie.todo.data.source.local.ToDoDatabase

private const val DB_NAME = "Tasks.db"

object ServiceLocator {
    private val lock = Any()
    private var database: ToDoDatabase? = null

    @Volatile
    var tasksRepository: TasksRepository? = null
        @VisibleForTesting set

    fun provideTasksRepository(context: Context): TasksRepository {
        synchronized(this) {
            return tasksRepository ?: createTasksRepository(context)
        }
    }

    private fun createTasksRepository(context: Context): TasksRepository {
        val newRepo =
            DefaultTasksRepository(createTaskLocalDataSource(context))
        tasksRepository = newRepo
        return newRepo
    }

    private fun createTaskLocalDataSource(context: Context): TasksDataSource {
        val database = database ?: createDataBase(context)
        return TasksLocalDataSource(database.taskDao())
    }

    private fun createDataBase(context: Context): ToDoDatabase {
        val result = Room.databaseBuilder(
            context.applicationContext,
            ToDoDatabase::class.java, DB_NAME
        ).build()
        database = result
        return result
    }

    @VisibleForTesting
    fun resetRepository() {
        synchronized(lock) {
            // Clear all data to avoid test pollution.
            database?.apply {
                clearAllTables()
                close()
            }
            database = null
            tasksRepository = null
        }
    }
}