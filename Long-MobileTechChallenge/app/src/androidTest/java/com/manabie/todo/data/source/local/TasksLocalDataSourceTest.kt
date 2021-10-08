package com.manabie.todo.data.source.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.manabie.todo.data.Result
import com.manabie.todo.data.entities.Task
import com.manabie.todo.data.succeeded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class TasksLocalDataSourceTest {

    private lateinit var localDataSource: TasksLocalDataSource
    private lateinit var database: ToDoDatabase

    @Before
    fun setup() {
        // using an in-memory database for testing, since it doesn't survive killing the process
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ToDoDatabase::class.java
        ).allowMainThreadQueries().build()

        localDataSource = TasksLocalDataSource(database.taskDao(), Dispatchers.Main)
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun getTasks_retrieveCompletedTasks() = runBlocking {
        // GIVEN - a new task saved in the database
        val newTask = Task("title", "description", true)
        localDataSource.saveTask(newTask)

        // WHEN  - Task retrieved by ID
        val result = localDataSource.getTasks(true)

        // THEN - Same task is returned
        MatcherAssert.assertThat(result.succeeded, CoreMatchers.`is`(true))
        result as Result.Success
        MatcherAssert.assertThat(result.data.size, CoreMatchers.`is`(1))
        MatcherAssert.assertThat(result.data[0].id, CoreMatchers.`is`(newTask.id))
        MatcherAssert.assertThat(result.data[0].title, CoreMatchers.`is`(newTask.title))
        MatcherAssert.assertThat(result.data[0].description, CoreMatchers.`is`(newTask.description))
        MatcherAssert.assertThat(result.data[0].isCompleted, CoreMatchers.`is`(true))
    }

    @Test
    fun getTasks_retrieveUnCompletedTasks() = runBlocking {
        // GIVEN - a new task saved in the database
        val newTask = Task("title", "description")
        localDataSource.saveTask(newTask)

        // WHEN  - Task retrieved by ID
        val result = localDataSource.getTasks(false)

        // THEN - Same task is returned
        MatcherAssert.assertThat(result.succeeded, CoreMatchers.`is`(true))
        result as Result.Success
        MatcherAssert.assertThat(result.data.size, CoreMatchers.`is`(1))
        MatcherAssert.assertThat(result.data[0].id, CoreMatchers.`is`(newTask.id))
        MatcherAssert.assertThat(result.data[0].title, CoreMatchers.`is`(newTask.title))
        MatcherAssert.assertThat(result.data[0].description, CoreMatchers.`is`(newTask.description))
        MatcherAssert.assertThat(result.data[0].isCompleted, CoreMatchers.`is`(false))
    }

    @Test
    fun saveTask_retrievesTask() = runBlocking {
        // GIVEN - a new task saved in the database
        val newTask = Task("title", "description", true)
        localDataSource.saveTask(newTask)

        // WHEN  - Task retrieved by ID
        val result = localDataSource.getTask(newTask.id)

        // THEN - Same task is returned
        MatcherAssert.assertThat(result.succeeded, CoreMatchers.`is`(true))
        result as Result.Success
        MatcherAssert.assertThat(result.data.id, CoreMatchers.`is`(newTask.id))
        MatcherAssert.assertThat(result.data.title, CoreMatchers.`is`(newTask.title))
        MatcherAssert.assertThat(result.data.description, CoreMatchers.`is`(newTask.description))
        MatcherAssert.assertThat(result.data.isCompleted, CoreMatchers.`is`(newTask.isCompleted))
    }

    @Test
    fun completeTask_retrievedTaskIsComplete() = runBlocking {
        // GIVEN - a new task saved in the database
        val newTask = Task("title", "description")
        localDataSource.saveTask(newTask)

        // WHEN  - Task retrieved by ID
        localDataSource.completeTask(newTask.id)
        val result = localDataSource.getTask(newTask.id)

        // THEN - Same task is returned
        MatcherAssert.assertThat(result.succeeded, CoreMatchers.`is`(true))
        result as Result.Success
        MatcherAssert.assertThat(result.data.id, CoreMatchers.`is`(newTask.id))
        MatcherAssert.assertThat(result.data.title, CoreMatchers.`is`(newTask.title))
        MatcherAssert.assertThat(result.data.description, CoreMatchers.`is`(newTask.description))
        MatcherAssert.assertThat(result.data.isCompleted, CoreMatchers.`is`(true))
    }

    @Test
    fun activateTask_retrievedTaskIsActive() = runBlocking {
        // GIVEN - a new task saved in the database
        val newTask = Task("title", "description", true)
        localDataSource.saveTask(newTask)

        // WHEN  - Task retrieved by ID
        localDataSource.activateTask(newTask.id)
        val result = localDataSource.getTask(newTask.id)

        // THEN - Same task is returned
        MatcherAssert.assertThat(result.succeeded, CoreMatchers.`is`(true))
        result as Result.Success
        MatcherAssert.assertThat(result.data.id, CoreMatchers.`is`(newTask.id))
        MatcherAssert.assertThat(result.data.title, CoreMatchers.`is`(newTask.title))
        MatcherAssert.assertThat(result.data.description, CoreMatchers.`is`(newTask.description))
        MatcherAssert.assertThat(result.data.isCompleted, CoreMatchers.`is`(false))
    }
}