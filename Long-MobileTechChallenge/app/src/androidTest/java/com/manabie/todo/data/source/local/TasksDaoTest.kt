package com.manabie.todo.data.source.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.manabie.todo.data.entities.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class TasksDaoTest {
    private lateinit var database: ToDoDatabase

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            ToDoDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertTaskAndGetById() = runBlocking {
        // GIVEN - insert a task
        val task = Task("title", "description")
        database.taskDao().insertTask(task)

        // WHEN - Get the task by id from the database
        val loaded = database.taskDao().getTaskById(task.id)

        // THEN - The loaded data contains the expected values
        MatcherAssert.assertThat<Task>(loaded as Task, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(loaded.id, `is`(task.id))
        MatcherAssert.assertThat(loaded.title, `is`(task.title))
        MatcherAssert.assertThat(loaded.description, `is`(task.description))
        MatcherAssert.assertThat(loaded.isCompleted, `is`(task.isCompleted))
    }

    @Test
    fun insertTaskReplacesOnConflict() = runBlocking {
        // GIVEN - insert a task
        val task = Task("title", "description")
        database.taskDao().insertTask(task)

        // When a task with the same id is inserted
        val newTask = Task("title2", "description2", true, task.id)
        database.taskDao().insertTask(newTask)

        // THEN - The loaded data contains the expected values
        val loaded = database.taskDao().getTaskById(task.id)
        MatcherAssert.assertThat(loaded?.id, `is`(task.id))
        MatcherAssert.assertThat(loaded?.title, `is`("title2"))
        MatcherAssert.assertThat(loaded?.description, `is`("description2"))
        MatcherAssert.assertThat(loaded?.isCompleted, `is`(true))
    }

    @Test
    fun insertTaskAndGetTasks() = runBlocking {
        // GIVEN - insert a task
        val task = Task("title", "description")
        database.taskDao().insertTask(task)

        // WHEN - Get tasks from the database
        val tasks = database.taskDao().getTasks(false)

        // THEN - There is only 1 task in the database, and contains the expected values
        MatcherAssert.assertThat(tasks.size, `is`(1))
        MatcherAssert.assertThat(tasks[0].id, `is`(task.id))
        MatcherAssert.assertThat(tasks[0].title, `is`(task.title))
        MatcherAssert.assertThat(tasks[0].description, `is`(task.description))
        MatcherAssert.assertThat(tasks[0].isCompleted, `is`(task.isCompleted))
    }

    @Test
    fun updateCompletedAndGetById() = runBlocking {
        // GIVEN - insert a task
        val task = Task("title", "description")
        database.taskDao().insertTask(task)

        // WHEN - Get tasks from the database
        database.taskDao().updateCompleted(task.id, true)

        // THEN - The loaded data contains the expected values
        val loaded = database.taskDao().getTaskById(task.id)
        MatcherAssert.assertThat(loaded?.id, `is`(task.id))
        MatcherAssert.assertThat(loaded?.title, `is`(task.title))
        MatcherAssert.assertThat(loaded?.description, `is`(task.description))
        MatcherAssert.assertThat(loaded?.isCompleted, `is`(true))
    }
}
