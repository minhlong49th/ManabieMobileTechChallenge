package com.manabie.todo.ui.newtask

import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.manabie.todo.MainActivity
import com.manabie.todo.R
import com.manabie.todo.data.Result
import com.manabie.todo.data.source.FakeRepository
import com.manabie.todo.data.source.TasksRepository
import com.manabie.todo.utils.ServiceLocator
import com.manabie.todo.utils.getTasksBlocking
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@MediumTest
class NewTaskActivityTest {

    private lateinit var taskRepository: TasksRepository

    @Before
    fun initRepository() {
        taskRepository = FakeRepository()
        ServiceLocator.tasksRepository = taskRepository
    }

    @Before
    fun setup() {
        launchActivity()
    }

    @After
    fun cleanupDb() = runBlocking { ServiceLocator.resetRepository() }

    @Test
    fun emptyTask_isNotSaved() {
        // WHEN - Enter invalid title and description combination and click save
        Espresso.onView(withId(R.id.edtTaskTitle)).perform(ViewActions.clearText())
        Espresso.onView(withId(R.id.edtDescription))
            .perform(ViewActions.clearText())
        Espresso.onView(withId(R.id.btnSave)).perform(ViewActions.click())

        // THEN - Entered Task is still displayed (a correct task would close it).
        Espresso.onView(withId(R.id.edtTaskTitle))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun validTask_isSaved() {
        // WHEN - Valid title and description combination and click save
        Espresso.onView(withId(R.id.edtTaskTitle))
            .perform(ViewActions.replaceText("new_task"))
        Espresso.onView(withId(R.id.edtDescription))
            .perform(ViewActions.replaceText("new_task_description"))
        Espresso.onView(withId(R.id.btnSave)).perform(ViewActions.click())

        // THEN - Verify that the repository saved the task
        val tasks = (taskRepository.getTasksBlocking() as Result.Success).data
        Assert.assertEquals(tasks.size, 1)
        Assert.assertEquals(tasks[0].title, "new_task")
        Assert.assertEquals(tasks[0].description, "new_task_description")
    }

    private fun launchActivity(): ActivityScenario<MainActivity>? {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        activityScenario.onActivity { activity ->
            (activity.findViewById(R.id.fab) as View).performClick()
        }
        return activityScenario
    }
}