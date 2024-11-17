package com.bignerdranch.android.pomodo

import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.bignerdranch.android.pomodo.database.DatabaseHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bignerdranch.android.pomodo.model.ToDoModel
import com.bignerdranch.android.pomodo.ui.theme.PomoDoTheme

class MainActivity : ComponentActivity() {

    private lateinit var db: DatabaseHandler




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = DatabaseHandler(this)
        db.openDatabase()

        setContent {
            PomoDoTheme {
                MainScreen(db = db)
            }
        }
    }


    @Composable
    fun MainScreen(db: DatabaseHandler) {
        var taskList by remember { mutableStateOf(listOf<ToDoModel>()) }
        var showDialog by remember { mutableStateOf(false) }

        // Load tasks once
        LaunchedEffect(Unit) {
            taskList = db.getAllTasks().reversed()
        }

        // Update task list after adding a task
        val updateTasks = {
            taskList = db.getAllTasks().reversed()
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                TaskList(tasks = taskList)

                FloatingActionButton(
                    onClick = { showDialog = true },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("+")
                }

                if (showDialog) {
                    AddTaskDialog(
                        onDismiss = { showDialog = false },
                        onTaskAdded = updateTasks,
                        db = db
                    )
                }
            }
        }
    }

    @Composable
    fun TaskList(tasks: List<ToDoModel>) {
        LazyColumn {
            items(tasks) { task ->
                Text(task.task)  // Display task
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AddTaskDialog(onDismiss: () -> Unit, onTaskAdded: () -> Unit, db: DatabaseHandler) {
        var newTaskText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                Button(
                    onClick = {
                        if (newTaskText.isNotBlank()) {
                            val newTask = ToDoModel(task = newTaskText)
                            db.insertTask(newTask)
                            onTaskAdded()
                            onDismiss()
                        }
                    }
                ) {
                    Text("Add Task")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("Cancel")
                }
            },
            title = { Text("Add New Task") },
            text = {
                TextField(
                    value = newTaskText,
                    onValueChange = { newTaskText = it },
                    label = { Text("Task description") }
                )
            }
        )
    }
}

// Mock DatabaseHandler for Preview (without Context)
class MockDatabaseHandler : DatabaseHandler(null) {
    override fun getAllTasks(): List<ToDoModel> {
        return listOf(ToDoModel(task = "Sample Task"))
    }

    override fun insertTask(task: ToDoModel) {
        // Mock inserting task, no database action needed for preview
    }

    override fun openDatabase() {
        // No need to mock openDatabase
    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    PomoDoTheme {
        MainScreen(db = MockDatabaseHandler())
    }
}

@Composable
fun MainScreen(db: MockDatabaseHandler) {

}