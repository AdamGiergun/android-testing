package com.example.android.architecture.blueprints.todoapp.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.succeeded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class TasksLocalDataSourceTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var localDataSource: TasksLocalDataSource
    private lateinit var database: ToDoDatabase

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ToDoDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        localDataSource = TasksLocalDataSource(
            database.taskDao(),
            Dispatchers.Main
        )
    }

    @After
    fun cleanUp() = database.close()

    @Test
    fun saveTask_retrievesTask() = runTest {
        // GIVEN - A new task saved in the database.
        val newTask = Task("title", "description", false)
        localDataSource.saveTask(newTask)

        // WHEN  - Task retrieved by ID.
        val result = localDataSource.getTask(newTask.id)

        // THEN - Same task is returned.
        MatcherAssert.assertThat(result.succeeded, `is`(true))
        result as Result.Success
        MatcherAssert.assertThat(result.data.title, `is`("title"))
        MatcherAssert.assertThat(result.data.description, `is`("description"))
        MatcherAssert.assertThat(result.data.isCompleted, `is`(false))
    }

    @Test
    fun completeTask_retrievedTaskIsComplete() = runTest {
        // Given a new task in the persistent repository
        val task = Task("title")
        localDataSource.saveTask(task)

        // When completed in the persistent repository
        localDataSource.completeTask(task)
        val result = localDataSource.getTask(task.id)

        // Then the task can be retrieved from the persistent repository and is complete
        MatcherAssert.assertThat(result.succeeded, `is`(true))
        result as Result.Success
        MatcherAssert.assertThat(result.data.title, `is`(task.title))
        MatcherAssert.assertThat(result.data.isCompleted, `is`(true))
    }
}