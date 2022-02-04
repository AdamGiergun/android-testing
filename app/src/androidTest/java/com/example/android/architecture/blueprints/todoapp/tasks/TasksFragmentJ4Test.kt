package com.example.android.architecture.blueprints.todoapp.tasks

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
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
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@MediumTest
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class TasksFragmentJ4Test {

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
    fun clickTask_navigateToDetailFragmentOne() = runTest(UnconfinedTestDispatcher()) {
        // GIVEN - On the tasks screen with two tasks
        repository.saveTask(Task("TITLE1", "DESCRIPTION1", false, "id1"))
        repository.saveTask(Task("TITLE2", "DESCRIPTION2", true, "id2"))

        val scenario = launchFragmentInContainer<TasksFragment>(Bundle(), R.style.AppTheme)

        val navController = mock(NavController::class.java)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
        //WHEN - Click on the first list item
        onView(withId(R.id.tasks_list))
            .perform(
                RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText("TITLE1")),
                    click()
                )
            )

        // THEN - Verify that we navigate to the first detail screen
        verify(navController).navigate(
            TasksFragmentDirections.actionTasksFragmentToTaskDetailFragment("id1")
        )
    }

    @Test
    fun clickAddTaskButton_navigateToAddEditFragment() = runTest(UnconfinedTestDispatcher()) {

        // GIVEN - On the tasks screen
        val scenario = launchFragmentInContainer<TasksFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        //WHEN - Click on the FAB button
        onView(withId(R.id.add_task_fab)).perform(click())

        // THEN - Verify that we navigate to the
        verify(navController).navigate(
            TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(
                null,
                getApplicationContext<Context>().getString(R.string.add_task))
        )
    }
}