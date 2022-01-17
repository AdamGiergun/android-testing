package com.example.android.architecture.blueprints.todoapp.statistics

import com.example.android.architecture.blueprints.todoapp.data.Task
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

internal class StatisticsUtilsJ5Test {

    private var tasks: List<Task>? = null
    private lateinit var result: StatsResult


    abstract inner class SetTasksList(private val tasksList: List<Task>?) {
        @BeforeEach
        fun setup() {
            tasks = tasksList
        }
    }

    abstract inner class GetActiveAndCompletedStats {
        @BeforeEach
        fun getResult() {
            result = getActiveAndCompletedStats(tasks)
        }
    }

    private fun compareResultWith(expectedResult: StatsResult) {
        assertEquals(
            result.completedTasksPercent,
            expectedResult.completedTasksPercent
        )
        assertEquals(
            result.activeTasksPercent,
            expectedResult.activeTasksPercent
        )
    }

    @Nested
    @DisplayName("Given tasks list is null")
    inner class Null : SetTasksList(null) {
        @Nested
        @DisplayName("When getActiveAndCompletedStats(tasks)")
        inner class TestResultOf() : GetActiveAndCompletedStats() {
            @Test
            @DisplayName("Then result is 0% active 0% completed tasks")
            fun compare() {
                compareResultWith(StatsResult(0f, 0f))
            }
        }
    }

    @Nested
    @DisplayName("Given empty tasks list")
    inner class EmptyTasksList : SetTasksList(emptyList()) {
        @Nested
        @DisplayName("When getActiveAndCompletedStats(tasks)")
        inner class TestResultOf() : GetActiveAndCompletedStats() {
            @Test
            @DisplayName("Then result is 0% active 0% completed tasks")
            fun compare() {
                compareResultWith(StatsResult(0f, 0f))
            }
        }
    }

    @Nested
    @DisplayName("Given tasks list with no active tasks")
    inner class NoActiveTasks : SetTasksList(
        listOf(
            Task("title", "desc", true),
            Task("title", "desc", true)
        )
    ) {
        @Nested
        @DisplayName("When getActiveAndCompletedStats(tasks)")
        inner class TestResultOf() : GetActiveAndCompletedStats() {
            @Test
            @DisplayName("Then result is 0% active 100% completed tasks")
            fun compare() {
                compareResultWith(StatsResult(0f, 100f))
            }
        }
    }

    @Nested
    @DisplayName("Given tasks list with no completed tasks")
    inner class NoCompletedTasks : SetTasksList(
        listOf(
            Task("title", "desc", false)
        )
    ) {
        @Nested
        @DisplayName("When getActiveAndCompletedStats(tasks)")
        inner class TestResultOf() : GetActiveAndCompletedStats() {
            @Test
            @DisplayName("Then result is 100% active 0% completed tasks")
            fun compare() {
                compareResultWith(StatsResult(100f, 0f))
            }
        }
    }

    @Nested
    @DisplayName("Given list of 5 mixed types tasks")
    inner class ListOfMixedTasks : SetTasksList(
        listOf(
            Task("title", "desc", isCompleted = true),
            Task("title", "desc", isCompleted = true),
            Task("title", "desc", isCompleted = true),
            Task("title", "desc", isCompleted = false),
            Task("title", "desc", isCompleted = false)
        )
    ) {
        @Nested
        @DisplayName("When getActiveAndCompletedStats(tasks)")
        inner class TestResultOf() : GetActiveAndCompletedStats() {
            @Test
            @DisplayName("Then result is 40% active 60% completed tasks")
            fun compare() {
                compareResultWith(StatsResult(40f, 60f))
            }
        }
    }
}