package com.bignerdranch.android.pomodo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bignerdranch.android.pomodo.Model.ToDoModel
import com.bignerdranch.android.pomodo.ui.theme.PomoDoTheme

class MainActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = DatabaseHandler(this)  // Pass context here
        db.openDatabase()

        setContent {
            PomoDoTheme {
                MainScreen(db = db)  // Pass db to MainScreen composable
            }
        }
    }

    @Composable
    fun MainScreen(db: DatabaseHandler) {
        var taskList by remember { mutableStateOf(listOf<ToDoModel>()) }
        var showDialog by remember { mutableStateOf(false) }

        // Load tasks once
        LaunchedEffect(Unit) {
            taskList = db.getAllTasks().reversed()  // Load tasks initially
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
                            db.insertTask(newTask)  // Insert new task
                            onTaskAdded()  // Refresh task list
                            onDismiss()  // Close dialog
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
        // Use the mocked database
        MainScreen(db = MockDatabaseHandler())
    }
}

@Composable
fun MainScreen(db: MockDatabaseHandler) {

}
