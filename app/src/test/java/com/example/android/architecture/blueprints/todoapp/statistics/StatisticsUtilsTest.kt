package com.example.android.architecture.blueprints.todoapp.statistics

import com.example.android.architecture.blueprints.todoapp.data.Task
import org.hamcrest.Matchers.`is`
import org.junit.Assert.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class StatisticsUtilsTest {

    @Test
    @DisplayName("Given emptyList when getActiveAndCompletedStats returns 0% completed 0% active tasks")
    fun test1() {
        val result = getActiveAndCompletedStats(emptyList())
        assertThat(result.completedTasksPercent, `is`(0f))
        assertThat(result.activeTasksPercent, `is`(0f))
    }

    @Test
    @DisplayName("Given no active tasks when getActiveAndCompletedStats returns 100% completed 0% active tasks")
    fun test2() {
        val tasks = listOf(
                Task("title", "desc", true)
        )
        val result = getActiveAndCompletedStats(tasks)
        assertThat(result.completedTasksPercent, `is`(100f))
        assertThat(result.activeTasksPercent, `is`(0f))
    }

    @Test
    @DisplayName("Given null when getActiveAndCompletedStats returns 0% completed 0% active tasks")
    fun test3() {
        val result = getActiveAndCompletedStats(null)
        assertThat(result.completedTasksPercent, `is`(0f))
        assertThat(result.activeTasksPercent, `is`(0f))
    }

    @Test
    @DisplayName("Given only active tasks when getActiveAndCompletedStats returns 0% completed 100% active tasks")
    fun test4() {
        val tasks = listOf(
                Task("title", "desc", false)
        )
        val result = getActiveAndCompletedStats(tasks)
        assertThat(result.completedTasksPercent, `is`(0f))
        assertThat(result.activeTasksPercent, `is`(100f))
    }

    @Test
    @DisplayName("Given 5 mixed tasks when getActiveAndCompletedStats returns 60% completed 40% active tasks")
    fun test5() {
        val tasks = listOf(
                Task("title", "desc", isCompleted = true),
                Task("title", "desc", isCompleted = true),
                Task("title", "desc", isCompleted = true),
                Task("title", "desc", isCompleted = false),
                Task("title", "desc", isCompleted = false)
        )
        val result = getActiveAndCompletedStats(tasks)
        assertThat(result.completedTasksPercent, `is`(60f))
        assertThat(result.activeTasksPercent, `is`(40f))
    }
}