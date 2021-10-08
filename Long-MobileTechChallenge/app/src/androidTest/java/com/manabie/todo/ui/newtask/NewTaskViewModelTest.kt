package com.manabie.todo.ui.newtask

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
class NewTaskViewModelTest {

    private val newTask = Task("Title New Task", "Description1 New Task")
    private val task1 = Task("Title1", "Description1")
    private val task2 = Task("Title2", "Description2", true)
    private val task3 = Task("Title3", "Description3", true)

    private lateinit var tasksViewModel: NewTaskViewModel
    private lateinit var tasksRepository: FakeRepository

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setViewModel() {
        tasksRepository = FakeRepository()
        tasksRepository.addTasks(task1, task2, task3)
        tasksViewModel = NewTaskViewModel(tasksRepository)
    }

    @Test
    fun saveNewTask_allInputDataIsNull_error() {
        saveTaskAndAssertSnackbarError(null, null)
    }

    @Test
    fun saveNewTask_allInputDataIsEmpty_error() {
        saveTaskAndAssertSnackbarError("", "")
    }

    @Test
    fun saveNewTask_emptyTitle_error() {
        saveTaskAndAssertSnackbarError("", "Some Task Description")
    }

    @Test
    fun saveNewTask_nullTitle_error() {
        saveTaskAndAssertSnackbarError(null, "Some Task Description")
    }

    @Test
    fun saveNewTask_emptyDescription_error() {
        saveTaskAndAssertSnackbarError("Title New Task", "")
    }

    @Test
    fun saveNewTask_nullDescription_error() {
        saveTaskAndAssertSnackbarError("Title New Task", null)
    }

    @Test
    fun saveNewTask_showsSuccessMessageUi() {
        (tasksViewModel).apply {
            this.title.value = newTask.title
            this.description.value = newTask.description
        }

        // When saving an incomplete task
        tasksViewModel.saveTask()

        // Then the snackbar shows an error
        val taskSavedEvent = tasksViewModel.taskSavedEvent.getOrAwaitValue()
        val taskSavedValue = taskSavedEvent.getContentIfNotHandled()
        Truth.assertThat(taskSavedValue).isNotNull()
    }

    private fun saveTaskAndAssertSnackbarError(title: String?, description: String?) {
        (tasksViewModel).apply {
            this.title.value = title
            this.description.value = description
        }

        // When saving an incomplete task
        tasksViewModel.saveTask()

        // Then the snackbar shows an error
        val value = tasksViewModel.snackbarText.getOrAwaitValue()
        val messageId = value.getContentIfNotHandled()
        Truth.assertThat(messageId).isNotNull()
        Truth.assertThat(messageId).isEqualTo(R.string.empty_task_message)
    }
}