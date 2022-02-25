package com.example.android.architecture.blueprints.todoapp.statistics

import com.example.android.architecture.blueprints.todoapp.InstantExecutorExtension
import com.example.android.architecture.blueprints.todoapp.MainCoroutineExtension
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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith


@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@ExtendWith(MainCoroutineExtension::class, InstantExecutorExtension::class)
class StatisticsViewModelJunit5Test {

    // Subject under test
    private lateinit var statisticsViewModel: StatisticsViewModel

    // Use a fake repository to be injected into the view model.
    private lateinit var tasksRepository: FakeRepository

    @BeforeEach
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