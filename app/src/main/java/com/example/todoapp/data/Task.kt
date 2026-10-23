package com.example.todoapp.data

data class Task(
    val id: Long,
    val title: String,
    val done: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

enum class TaskFilter(val label: String) {
    ALL("Toate"),
    ACTIVE("Active"),
    DONE("Finalizate");

    fun apply(tasks: List<Task>): List<Task> = when (this) {
        ALL -> tasks
        ACTIVE -> tasks.filter { !it.done }
        DONE -> tasks.filter { it.done }
    }
}
