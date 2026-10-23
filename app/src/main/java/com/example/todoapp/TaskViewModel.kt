package com.example.todoapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.todoapp.data.Task
import com.example.todoapp.data.TaskFilter
import com.example.todoapp.data.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TaskRepository(application)

    private val _tasks = MutableStateFlow(repository.load())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _filter = MutableStateFlow(TaskFilter.ALL)
    val filter: StateFlow<TaskFilter> = _filter.asStateFlow()

    fun addTask(title: String) {
        val clean = title.trim()
        if (clean.isEmpty()) return
        val nextId = (_tasks.value.maxOfOrNull { it.id } ?: 0L) + 1
        modify { it + Task(id = nextId, title = clean) }
    }

    fun toggle(id: Long) = modify { list ->
        list.map { if (it.id == id) it.copy(done = !it.done) else it }
    }

    fun delete(id: Long) = modify { list -> list.filterNot { it.id == id } }

    fun clearCompleted() = modify { list -> list.filterNot { it.done } }

    fun clearAll() = modify { emptyList() }

    fun setFilter(filter: TaskFilter) {
        _filter.value = filter
    }

    private fun modify(transform: (List<Task>) -> List<Task>) {
        _tasks.update(transform)
        repository.save(_tasks.value)
    }
}
