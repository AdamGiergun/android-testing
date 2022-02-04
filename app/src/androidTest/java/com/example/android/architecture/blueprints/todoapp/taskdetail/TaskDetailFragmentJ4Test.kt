package com.example.android.architecture.blueprints.todoapp.taskdetail

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.ServiceLocator
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.FakeRepository
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.core.IsNot.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class TaskDetailFragmentJ4Test {

    private lateinit var repository: TasksRepository

    @Before
    fun initRepository() {
        repository = FakeRepository()
        ServiceLocator.tasksRepository = repository
    }

    @After
    fun cleanupDb() = runTest(UnconfinedTestDispatcher()) {
        ServiceLocator.resetRepository()
    }

    @Test
    fun activeTaskDetails_DisplayedInUi() = runTest(UnconfinedTestDispatcher()) {
        // GIVEN - Add active (incomplete) task to the DB
        val activeTask = Task("Active task", "AndroidX Rocks", false)
        repository.saveTask(activeTask)

        // WHEN - Details fragment launched to display task
        launchDetailsFragment(activeTask.id)

        // THEN - Task details are displayed on the screen
        // make sure that the title/description are both shown and correct
        assertThatTaskIsDisplayedCorrectly("Active task", "AndroidX Rocks", false)
    }

    @Test
    fun completedTaskDetails_DisplayedInUi() = runTest(UnconfinedTestDispatcher()) {
        // GIVEN - Add completed task to the DB
        val completedTask = Task("Completed task", "AndroidX Rocks Even More", true)
        repository.saveTask(completedTask)

        // WHEN - Details fragment launched to display task
        launchDetailsFragment(completedTask.id)

        // THEN - Task details are displayed on the screen
        // make sure that the title/description are both shown and correct
        assertThatTaskIsDisplayedCorrectly("Completed task", "AndroidX Rocks Even More", true)
    }

    private fun launchDetailsFragment(taskId: String) {
        val bundle = TaskDetailFragmentArgs(taskId).toBundle()
        launchFragmentInContainer<TaskDetailFragment>(bundle, R.style.AppTheme)
    }

    private fun assertThatTaskIsDisplayedCorrectly(
        taskTitle: String,
        taskDescription: String,
        taskIsCompleted: Boolean
    ) {
        onView(withId(R.id.task_detail_title_text)).check(matches(isDisplayed()))
        onView(withId(R.id.task_detail_title_text)).check(matches(withText(taskTitle)))

        onView(withId(R.id.task_detail_description_text)).check(matches(isDisplayed()))
        onView(withId(R.id.task_detail_description_text)).check(matches(withText(taskDescription)))

        onView(withId(R.id.task_detail_complete_checkbox)).check(matches(isDisplayed()))
        onView(withId(R.id.task_detail_complete_checkbox)).check(
            matches(if (taskIsCompleted) isChecked() else not(isChecked()))
        )
    }
}