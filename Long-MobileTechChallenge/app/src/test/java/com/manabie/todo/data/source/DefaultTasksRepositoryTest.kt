package com.manabie.todo.data.source

import com.google.common.truth.Truth
import com.manabie.todo.data.Result
import com.manabie.todo.data.entities.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class DefaultTasksRepositoryTest {

    private val newTask = Task("Title new", "Description new")
    private val task1 = Task("Title1", "Description1")
    private val task2 = Task("Title2", "Description2", true)
    private val localTasks = listOf(task1, task2)

    private lateinit var tasksLocalDataSource: FakeDataSource
    private lateinit var tasksRepository: DefaultTasksRepository

    @Before
    fun createRepository() {
        tasksLocalDataSource = FakeDataSource(localTasks.toMutableList())
        tasksRepository = DefaultTasksRepository(tasksLocalDataSource, Dispatchers.Main)
    }

    @Test
    fun getTasks_completedTask() = runBlocking {
        val tasks = tasksRepository.getTasks(true)

        Truth.assertThat(tasks is Result.Success).isTrue()
    }


    @Test
    fun getTasks_untCompletedTask() = runBlocking {
        val tasks = tasksRepository.getTasks(false)

        Truth.assertThat(tasks is Result.Success).isTrue()
    }

    @Test
    fun saveTask_savesToLocal() = runBlocking {
        // Make sure newTask is not in the datasources
        Truth.assertThat(tasksLocalDataSource.tasks).doesNotContain(newTask)

        // When a task is saved to the tasks repository
        tasksRepository.saveTask(newTask)

        // Then the data sources are called
        Truth.assertThat(tasksLocalDataSource.tasks).contains(newTask)
    }

    @Test
    fun getTask() = runBlocking {
        val task = (tasksRepository.getTask(task1.id) as Result.Success).data

        Truth.assertThat(task.id).isNotEmpty()
        Truth.assertThat(task.title).isEqualTo(task1.title)
        Truth.assertThat(task.description).isEqualTo(task1.description)
    }

    @Test
    fun completeTask() = runBlocking {
        Truth.assertThat(tasksLocalDataSource.tasks).contains(task1)

        val task = (tasksRepository.getTask(task1.id) as Result.Success).data
        Truth.assertThat(task.isCompleted).isEqualTo(false)

        tasksRepository.completeTask(task1)

        val taskAfterMarkCompleted = (tasksRepository.getTask(task1.id) as Result.Success).data
        Truth.assertThat(taskAfterMarkCompleted.id).isNotEmpty()
        Truth.assertThat(taskAfterMarkCompleted.title).isEqualTo(task1.title)
        Truth.assertThat(taskAfterMarkCompleted.description).isEqualTo(task1.description)
        Truth.assertThat(taskAfterMarkCompleted.isCompleted).isEqualTo(true)
    }

    @Test
    fun activateTask() = runBlocking {
        Truth.assertThat(tasksLocalDataSource.tasks).contains(task2)

        val task = (tasksRepository.getTask(task2.id) as Result.Success).data
        Truth.assertThat(task.isCompleted).isEqualTo(true)

        tasksRepository.activateTask(task2)

        val taskAfterMarkActivated = (tasksRepository.getTask(task2.id) as Result.Success).data
        Truth.assertThat(taskAfterMarkActivated.id).isNotEmpty()
        Truth.assertThat(taskAfterMarkActivated.title).isEqualTo(task2.title)
        Truth.assertThat(taskAfterMarkActivated.description).isEqualTo(task2.description)
        Truth.assertThat(taskAfterMarkActivated.isCompleted).isEqualTo(false)
    }
}