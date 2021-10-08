package com.manabie.todo.ui.completed

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth
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
class CompletedTaskViewModelTest {
    private val task1 = Task("Title1", "Description1")
    private val task2 = Task("Title2", "Description2", true)
    private val task3 = Task("Title3", "Description3", true)

    private lateinit var tasksViewModel: CompletedTaskViewModel
    private lateinit var tasksRepository: FakeRepository

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setViewModel() {
        tasksRepository = FakeRepository()
        tasksRepository.addTasks(task1, task2, task3)
        tasksViewModel = CompletedTaskViewModel(tasksRepository)
    }

    @Test
    fun getTasks_CompletedTask() {
        tasksViewModel.getCompletedTask()

        val tasks = tasksViewModel.items.getOrAwaitValue()

        Truth.assertThat(tasks).hasSize(2)
        Truth.assertThat(tasks[0].title).isEqualTo(task2.title)
        Truth.assertThat(tasks[0].description).isEqualTo(task2.description)
        Truth.assertThat(tasks[0].isCompleted).isEqualTo(true)
    }

    @Test
    fun openTask() {
        tasksViewModel.openTask(task2.id)

        val value = tasksViewModel.item.getOrAwaitValue()
        val task = value.getContentIfNotHandled()

        Truth.assertThat(task).isNotNull()
        Truth.assertThat(task?.id).isEqualTo(task2.id)
    }

    @Test
    fun activateTask_dataAndSnackbarUpdated() {
        tasksViewModel.activateTask(task2)

        // The snackbar is updated
        val value = tasksViewModel.snackbarText.getOrAwaitValue()
        val messageId = value.getContentIfNotHandled()
        Truth.assertThat(messageId).isNotNull()
        Truth.assertThat(messageId).isEqualTo(R.string.task_marked_complete)

        // Verify the task is completed
        val taskAfterUpdate = tasksRepository.tasksServiceData[task2.id]
        Truth.assertThat(taskAfterUpdate).isNotNull()
        Truth.assertThat(taskAfterUpdate?.isCompleted).isFalse()
    }
}