package com.example.android.architecture.blueprints.todoapp.data.source

import android.content.Context
import android.provider.DocumentsContract
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.example.android.architecture.blueprints.todoapp.data.source.local.TasksLocalDataSource
import com.example.android.architecture.blueprints.todoapp.data.source.local.ToDoDatabase
import com.example.android.architecture.blueprints.todoapp.data.source.remote.TasksRemoteDataSource
import kotlinx.coroutines.runBlocking

object ServiceLocator {

    private var database: ToDoDatabase? = null

    private val lock = Any()

    @Volatile
    var tasksRepository: TasksRepository? = null
        @VisibleForTesting set

    fun provideTasksRepository(context: Context): TasksRepository {
        synchronized(this) {
            return tasksRepository ?: createTasksRepository(context)
        }
    }

    private fun createTasksRepository(context: Context): TasksRepository {
        DefaultTasksRepository(TasksRemoteDataSource, createTaskLocalDataSource(context)).let {
            tasksRepository = it
            return it
        }
    }

    private fun createTaskLocalDataSource(context: Context): TasksDataSource {
        (database ?: createDataBase(context)).let {
            return TasksLocalDataSource(it.taskDao())
        }
    }

    private fun createDataBase(context: Context): ToDoDatabase {
        Room.databaseBuilder(
                context.applicationContext,
                ToDoDatabase::class.java,
                "Tasks.db"
        ).build().let {
            database = it
            return it
        }
    }

    @VisibleForTesting
    fun resetRepository() {
        synchronized(lock) {
            runBlocking {
                TasksRemoteDataSource.deleteAllTasks()
            }
            // Clear all data to avoid test pollution.
            database?.apply {
                clearAllTables()
                close()
            }
            database = null
            tasksRepository = null
        }
    }
}