package com.example.todoapp

import com.example.todoapp.data.Task
import com.example.todoapp.data.TaskFilter
import org.junit.Assert.assertEquals
import org.junit.Test

class TaskFilterTest {
    private val tasks = listOf(
        Task(1, "A"),
        Task(2, "B", done = true),
        Task(3, "C")
    )

    @Test
    fun all_returnsEverything() {
        assertEquals(tasks, TaskFilter.ALL.apply(tasks))
    }

    @Test
    fun active_returnsOnlyUnfinished() {
        assertEquals(listOf(1L, 3L), TaskFilter.ACTIVE.apply(tasks).map { it.id })
    }

    @Test
    fun done_returnsOnlyFinished() {
        assertEquals(listOf(2L), TaskFilter.DONE.apply(tasks).map { it.id })
    }
}
