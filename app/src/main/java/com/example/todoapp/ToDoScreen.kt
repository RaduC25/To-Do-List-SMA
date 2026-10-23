package com.example.todoapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.data.Task
import com.example.todoapp.data.TaskFilter
import com.example.todoapp.ui.theme.ToDoAppTheme

@Composable
fun ToDoScreen(viewModel: TaskViewModel = viewModel()) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val filter by viewModel.filter.collectAsStateWithLifecycle()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        ToDoContent(
            tasks = tasks,
            filter = filter,
            onAdd = viewModel::addTask,
            onToggle = viewModel::toggle,
            onDelete = viewModel::delete,
            onFilterChange = viewModel::setFilter,
            onClearCompleted = viewModel::clearCompleted,
            onClearAll = viewModel::clearAll,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun ToDoContent(
    tasks: List<Task>,
    filter: TaskFilter,
    onAdd: (String) -> Unit,
    onToggle: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    onFilterChange: (TaskFilter) -> Unit,
    onClearCompleted: () -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    var newTitle by rememberSaveable { mutableStateOf("") }
    var confirmClearAll by rememberSaveable { mutableStateOf(false) }
    val doneCount = tasks.count { it.done }
    val visibleTasks = filter.apply(tasks)

    fun submit() {
        if (newTitle.isBlank()) return
        onAdd(newTitle)
        newTitle = ""
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Lista mea de sarcini",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
            )
            if (tasks.isNotEmpty()) {
                TextButton(onClick = { confirmClearAll = true }) { Text("Șterge tot") }
            }
        }
        Text(
            "$doneCount / ${tasks.size} finalizate",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (tasks.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { doneCount.toFloat() / tasks.size },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = newTitle,
                onValueChange = { newTitle = it },
                label = { Text("Sarcină nouă") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { submit() }),
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = { submit() }, enabled = newTitle.isNotBlank()) {
                Text("Adaugă")
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            TaskFilter.entries.forEach { option ->
                FilterChip(
                    selected = filter == option,
                    onClick = { onFilterChange(option) },
                    label = { Text(option.label) }
                )
            }
            Spacer(Modifier.weight(1f))
            if (doneCount > 0) {
                TextButton(onClick = onClearCompleted) { Text("Curăță") }
            }
        }

        Spacer(Modifier.height(8.dp))

        if (visibleTasks.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    when {
                        tasks.isEmpty() -> "Nicio sarcină încă. Adaugă una mai sus!"
                        filter == TaskFilter.ACTIVE -> "Toate sarcinile sunt finalizate 🎉"
                        else -> "Nicio sarcină finalizată."
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(visibleTasks, key = { it.id }) { task ->
                    TaskRow(
                        task = task,
                        onToggle = { onToggle(task.id) },
                        onDelete = { onDelete(task.id) },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
    }


    if (confirmClearAll) {
        AlertDialog(
            onDismissRequest = { confirmClearAll = false },
            title = { Text("Ștergi toate sarcinile?") },
            text = { Text("Lista va fi golită complet. Acțiunea nu poate fi anulată.") },
            confirmButton = {
                TextButton(onClick = {
                    onClearAll()
                    confirmClearAll = false
                }) { Text("Șterge tot") }
            },
            dismissButton = {
                TextButton(onClick = { confirmClearAll = false }) { Text("Renunță") }
            }
        )
    }
}

@Composable
private fun TaskRow(
    task: Task,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Checkbox(checked = task.done, onCheckedChange = { onToggle() })
            Text(
                task.title,
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (task.done) TextDecoration.LineThrough else null,
                color = if (task.done) MaterialTheme.colorScheme.onSurfaceVariant
                else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onDelete) { Text("Șterge") }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ToDoContentPreview() {
    ToDoAppTheme {
        ToDoContent(
            tasks = listOf(
                Task(1, "Cumpără lapte"),
                Task(2, "Termină laboratorul SMA", done = true),
                Task(3, "Sună-l pe Andrei")
            ),
            filter = TaskFilter.ALL,
            onAdd = {},
            onToggle = {},
            onDelete = {},
            onFilterChange = {},
            onClearCompleted = {},
            onClearAll = {}
        )
    }
}
