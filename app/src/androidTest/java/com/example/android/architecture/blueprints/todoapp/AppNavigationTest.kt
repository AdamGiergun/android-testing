package com.example.android.architecture.blueprints.todoapp

import android.app.Activity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.contrib.DrawerMatchers.isOpen
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import com.example.android.architecture.blueprints.todoapp.tasks.TasksActivity
import com.example.android.architecture.blueprints.todoapp.util.DataBindingIdlingResource
import com.example.android.architecture.blueprints.todoapp.util.EspressoIdlingResource
import com.example.android.architecture.blueprints.todoapp.util.monitorActivity
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@InternalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@LargeTest
class AppNavigationTest {

    private lateinit var tasksRepository: TasksRepository

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun init() {
        tasksRepository =
            ServiceLocator.provideTasksRepository(ApplicationProvider.getApplicationContext())
    }

    @After
    fun reset() {
        ServiceLocator.resetRepository()
    }

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(
            EspressoIdlingResource.countingIdlingResource,
            dataBindingIdlingResource
        )
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(
            EspressoIdlingResource.countingIdlingResource,
            dataBindingIdlingResource
        )
    }

    @Test
    fun tasksScreen_clickOnDrawerIcon_OpensNavigation() {
        // Start the Tasks screen.
        ActivityScenario.launch(TasksActivity::class.java).run {
            dataBindingIdlingResource.monitorActivity(this)

            // 1. Check that left drawer is closed at startup.
            onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(GravityCompat.START)))

            // 2. Open drawer by clicking drawer icon.
            onView(
                withContentDescription(
                    getToolbarNavigationContentDescription()
                )
            ).perform(click())

            // 3. Check if drawer is open.
            onView(withId(R.id.drawer_layout))
                .check(matches(isOpen(GravityCompat.START)))

            // When using ActivityScenario.launch(), always call close()
            close()
        }
    }

    @Test
    fun taskDetailScreen_doubleUpButton() = runBlocking {
        val task = Task("Up button", "Description")
        tasksRepository.saveTask(task)

        // Start the Tasks screen.
        ActivityScenario.launch(TasksActivity::class.java).run {
            dataBindingIdlingResource.monitorActivity(this)

            // 1. Click on the task on the list.
            onView(withText("Up button")).perform(click())

            // 2. Click on the edit task button.
            onView(withId(R.id.edit_task_fab)).perform(click())

            // 3. Confirm that if we click Up button once, we end up back at the task details page.
            onView(
                withContentDescription(
                    getToolbarNavigationContentDescription()
                )
            ).perform(click())
            onView(withId(R.id.task_detail_title_text)).check(matches(isDisplayed()))

            // 4. Confirm that if we click Up button a second time, we end up back at the home screen.
            onView(
                withContentDescription(
                    getToolbarNavigationContentDescription()
                )
            ).perform(click())
            onView(withId(R.id.tasks_container_layout)).check(matches(isDisplayed()))

            // When using ActivityScenario.launch(), always call close().
            close()
        }
    }


    @Test
    fun taskDetailScreen_doubleBackButton() = runBlocking {
        val task = Task("Back button", "Description")
        tasksRepository.saveTask(task)

        // Start Tasks screen.
        ActivityScenario.launch(TasksActivity::class.java).run {
            dataBindingIdlingResource.monitorActivity(this)

            // 1. Click on the task on the list.
            onView(withText("Back button")).perform(click())

            // 2. Click on the Edit task button.
            onView(withId(R.id.edit_task_fab)).perform(click())

            // 3. Confirm that if we click Back once, we end up back at the task details page.
            pressBack()
            onView(withId(R.id.task_detail_title_text)).check(matches(isDisplayed()))

            // 4. Confirm that if we click Back a second time, we end up back at the home screen.
            pressBack()
            onView(withId(R.id.tasks_container_layout)).check(matches(isDisplayed()))

            // When using ActivityScenario.launch(), always call close()
            close()
        }
    }
}

private fun <T : Activity> ActivityScenario<T>.getToolbarNavigationContentDescription(): String {

    var description = ""
    onActivity {
        description = it.findViewById<Toolbar>(R.id.toolbar)
            .navigationContentDescription as String
    }
    return description
}