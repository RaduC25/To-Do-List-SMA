package com.example.todoapp.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/** Saves tasks as JSON in SharedPreferences so they survive app restarts. */
class TaskRepository(context: Context) {
    private val prefs = context.getSharedPreferences("tasks", Context.MODE_PRIVATE)

    fun load(): List<Task> {
        val raw = prefs.getString(KEY_TASKS, null) ?: return emptyList()
        return try {
            val array = JSONArray(raw)
            List(array.length()) { i ->
                val o = array.getJSONObject(i)
                Task(
                    id = o.getLong("id"),
                    title = o.getString("title"),
                    done = o.getBoolean("done"),
                    createdAt = o.optLong("createdAt", 0L)
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun save(tasks: List<Task>) {
        val array = JSONArray()
        tasks.forEach { task ->
            array.put(
                JSONObject()
                    .put("id", task.id)
                    .put("title", task.title)
                    .put("done", task.done)
                    .put("createdAt", task.createdAt)
            )
        }
        prefs.edit().putString(KEY_TASKS, array.toString()).apply()
    }

    private companion object {
        const val KEY_TASKS = "tasks_json"
    }
}
