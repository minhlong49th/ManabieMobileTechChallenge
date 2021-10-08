package com.manabie.todo.ui.task

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.manabie.todo.R
import com.manabie.todo.data.entities.Task
import com.manabie.todo.data.source.FakeRepository
import com.manabie.todo.utils.getOrAwaitValue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class TaskViewModelTest {

    private val task1 = Task("Title1", "Description1")
    private val task2 = Task("Title2", "Description2", true)
    private val task3 = Task("Title3", "Description3", true)

    private lateinit var tasksViewModel: TaskViewModel
    private lateinit var tasksRepository: FakeRepository

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setViewModel() {
        tasksRepository = FakeRepository()
        tasksRepository.addTasks(task1, task2, task3)
        tasksViewModel = TaskViewModel(tasksRepository)
    }

    @Test
    fun getTasks_UnCompletedTask() {
        tasksViewModel.getTasks()

        val tasks = tasksViewModel.items.getOrAwaitValue()

        assertThat(tasks).hasSize(1)
        assertThat(tasks[0].title).isEqualTo(task1.title)
        assertThat(tasks[0].description).isEqualTo(task1.description)
        assertThat(tasks[0].isCompleted).isEqualTo(false)
    }

    @Test
    fun completeTask_dataAndSnackbarUpdated() {
        tasksViewModel.completeTask(task1)

        // The snackbar is updated
        val value = tasksViewModel.snackbarText.getOrAwaitValue()
        val messageId = value.getContentIfNotHandled()
        assertThat(messageId).isNotNull()
        assertThat(messageId).isEqualTo(R.string.task_marked_complete)

        // Verify the task is completed
        val taskAfterUpdate = tasksRepository.tasksServiceData[task1.id]
        assertThat(taskAfterUpdate).isNotNull()
        assertThat(taskAfterUpdate?.isCompleted).isTrue()
    }

    @Test
    fun openTask() {
        tasksViewModel.openTask(task1.id)

        val value = tasksViewModel.item.getOrAwaitValue()
        val task = value.getContentIfNotHandled()

        assertThat(task).isNotNull()
        assertThat(task?.id).isEqualTo(task1.id)
    }
}