package com.example.android.architecture.blueprints.todoapp.tasks

import com.example.android.architecture.blueprints.todoapp.*
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.FakeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(MainCoroutineExtension::class, InstantExecutorExtension::class)
class TasksViewModelJunit5Test {

    private lateinit var tasksRepository: FakeRepository

    // Subject under test
    private lateinit var tasksViewModel: TasksViewModel

    @BeforeEach
    fun setupViewModel() {
        tasksRepository = FakeRepository()
        val task1 = Task("Title1", "Description1")
        val task2 = Task("Title2", "Description2", true)
        val task3 = Task("Title3", "Description3", true)
        tasksRepository.addTasks(task1, task2, task3)

        tasksViewModel = TasksViewModel(tasksRepository)
    }

    @Test
    fun addNewTask_setsNewTaskEvent() {

        // When adding a new task
        tasksViewModel.addNewTask()

        // Then the new task event is triggered
        val value = tasksViewModel.newTaskEvent.getOrAwaitValue()
        assertThat(value.getContentIfNotHandled(), not(nullValue()))
    }

    @Test
    fun setFilterAllTasks_tasksAddViewVisible() {

        // When the filter type is ALL_TASKS
        tasksViewModel.setFiltering(TasksFilterType.ALL_TASKS)

        // Then the "Add task" action is visible
        assertThat(tasksViewModel.tasksAddViewVisible.getOrAwaitValue(), `is`(true))
        // simpler alternative
        //assert(tasksViewModel.tasksAddViewVisible.getOrAwaitValue())
    }

    @Test
    fun completeTask_dataAndSnackbarUpdated() = runTest {
        // With a repository that has an active tasks
        val task = Task("Title", "Description")
        tasksRepository.addTasks(task)

        // Complete task
        tasksViewModel.completeTask(task, true)
        advanceUntilIdle()

        // Verify that the task is completed
        assertThat(tasksRepository.tasksServiceData[task.id]?.isCompleted, `is`(true))

        // The snackbar is updated
        val snackbarText: Event<Int> = tasksViewModel.snackbarText.getOrAwaitValue()
        assertThat(snackbarText.getContentIfNotHandled(), `is`(R.string.task_marked_complete))
    }
}