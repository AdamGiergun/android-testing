package com.example.android.architecture.blueprints.todoapp.statistics

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android.architecture.blueprints.todoapp.MainCoroutineRule
import com.example.android.architecture.blueprints.todoapp.data.source.FakeRepository
import com.example.android.architecture.blueprints.todoapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class StatisticsViewModelTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Subject under test
    private lateinit var statisticsViewModel: StatisticsViewModel

    // Use a fake repository to be injected into the view model.
    private lateinit var tasksRepository: FakeRepository

    @Before
    fun setupViewModel() {
        // Initialise the repository with no tasks.
        tasksRepository = FakeRepository()

        statisticsViewModel = StatisticsViewModel(tasksRepository)
    }

    @Test
    fun loadTasks_loading() = runTest {
        withContext(StandardTestDispatcher(testScheduler)) {
            statisticsViewModel.refresh()
            assertThat(statisticsViewModel.dataLoading.getOrAwaitValue(), `is`(true))
            advanceUntilIdle()
            assertThat(statisticsViewModel.dataLoading.getOrAwaitValue(), `is`(false))
        }
    }

    @Test
    fun loadStatisticsWhenTasksAreUnavailable_callErrorToDisplay() = runTest {
        // Make the repository return errors
        tasksRepository.setReturnError(true)

        statisticsViewModel.refresh()
        advanceUntilIdle()

        // Then an error message is shown
        assertThat(statisticsViewModel.empty.getOrAwaitValue(), `is`(true))
        assertThat(statisticsViewModel.error.getOrAwaitValue(), `is`(true))
    }
}