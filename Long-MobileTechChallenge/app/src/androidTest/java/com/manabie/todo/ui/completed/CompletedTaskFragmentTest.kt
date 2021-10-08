package com.manabie.todo.ui.completed

import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
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

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@MediumTest
class CompletedTaskFragmentTest {
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
        val newTask = Task("TITLE1", "DESCRIPTION1", true)
        taskRepository.saveTaskBlocking(newTask)

        // WHEN - On startup
        launchActivity()

        // THEN - Verify task is displayed on screen
        Espresso.onView(ViewMatchers.withText(newTask.title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun markTaskAsActivate() {
        // GIVEN - One task already in the repository
        val newTask = Task("TITLE1", "DESCRIPTION1", true)
        taskRepository.saveTaskBlocking(newTask)

        // THEN - Verify task is displayed on screen
        launchActivity()
        Espresso.onView(ViewMatchers.withText(newTask.title)).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed())
        )

        // Mark the task as complete
        Espresso.onView(checkboxWithText(newTask.title)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(newTask.title)).check(ViewAssertions.doesNotExist())
    }

    @Test
    fun displayDetailTaskDialog() {
        // GIVEN - One task already in the repository
        val newTask = Task("TITLE1", "DESCRIPTION1", true)
        taskRepository.saveTaskBlocking(newTask)

        // THEN - Verify task is displayed on screen
        launchActivity()

        // Open it in details view
        Espresso.onView(ViewMatchers.withText(newTask.title)).perform(ViewActions.click())

        // Mark the task as complete
        Espresso.onView(ViewMatchers.withText(newTask.title)).inRoot(RootMatchers.isDialog()).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed())
        )
        Espresso.onView(ViewMatchers.withText(newTask.description)).inRoot(RootMatchers.isDialog())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }

    private fun launchActivity(): ActivityScenario<MainActivity>? {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        activityScenario.onActivity { activity ->
            (activity.findViewById(R.id.navigation_incomplete) as View).performClick()
        }
        return activityScenario
    }

    private fun checkboxWithText(text: String): Matcher<View> {
        return CoreMatchers.allOf(
            ViewMatchers.withId(R.id.cbComplete),
            ViewMatchers.hasSibling(ViewMatchers.withText(text))
        )
    }
}