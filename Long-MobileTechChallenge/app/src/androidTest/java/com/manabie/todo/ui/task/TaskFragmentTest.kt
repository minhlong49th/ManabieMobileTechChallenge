package com.manabie.todo.ui.task

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.manabie.todo.MainActivity
import com.manabie.todo.R
import com.manabie.todo.data.entities.Task
import com.manabie.todo.data.source.FakeRepository
import com.manabie.todo.data.source.TasksRepository
import com.manabie.todo.utils.ServiceLocator
import com.manabie.todo.utils.saveTaskBlocking
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.LooperMode
import org.robolectric.annotation.TextLayoutMode

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@MediumTest
class TaskFragmentTest {

    private lateinit var taskRepository: TasksRepository

    @Before
    fun initRepository() {
        taskRepository = FakeRepository()
        ServiceLocator.tasksRepository = taskRepository
    }

    @After
    fun cleanupDb() = runBlocking { ServiceLocator.resetRepository() }

    @Test
    fun displayTask_whenRepositoryHasData() {
        // GIVEN - One task already in the repository
        val newTask = Task("TITLE1", "DESCRIPTION1")
        taskRepository.saveTaskBlocking(newTask)

        // WHEN - On startup
        launchActivity()

        // THEN - Verify task is displayed on screen
        Espresso.onView(withText(newTask.title))
            .check(matches(isDisplayed()))
    }

    @Test
    fun markTaskAsComplete() {
        // GIVEN - One task already in the repository
        val newTask = Task("TITLE1", "DESCRIPTION1")
        taskRepository.saveTaskBlocking(newTask)

        // THEN - Verify task is displayed on screen
        launchActivity()
        Espresso.onView(withText(newTask.title)).check(matches(isDisplayed()))

        // Mark the task as complete
        Espresso.onView(checkboxWithText(newTask.title)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.rcyTask)).check(matches(hasChildCount(0)))

    }

    @Test
    fun displayDetailTaskDialog() {
        // GIVEN - One task already in the repository
        val newTask = Task("TITLE1", "DESCRIPTION1")
        taskRepository.saveTaskBlocking(newTask)

        // THEN - Verify task is displayed on screen
        launchActivity()

        // Open it in details view
        Espresso.onView(withText(newTask.title)).perform(ViewActions.click())

        // Mark the task as complete
        Espresso.onView(withText(newTask.title)).inRoot(isDialog()).check(matches(isDisplayed()))
        Espresso.onView(withText(newTask.description)).inRoot(isDialog())
            .check(matches(isDisplayed()))

    }

    @Test
    fun clickAddTaskButton_navigateToNewTaskActivity() {
        // THEN - Verify task is displayed on screen
        launchActivity()

        // Open create new view
        Espresso.onView(withId(R.id.fab)).perform(ViewActions.click())

        // Mark the task as complete
        Espresso.onView(withText("Add New Task")).check(matches(isDisplayed()))
    }

    private fun launchActivity(): ActivityScenario<MainActivity>? {
        val activityScenario = launch(MainActivity::class.java)
        activityScenario.onActivity { activity ->
            // Disable animations in RecyclerView
            (activity.findViewById(R.id.rcyTask) as RecyclerView).itemAnimator = null
        }
        return activityScenario
    }

    private fun checkboxWithText(text: String): Matcher<View> {
        return CoreMatchers.allOf(
            withId(R.id.cbComplete),
            hasSibling(withText(text))
        )
    }

}