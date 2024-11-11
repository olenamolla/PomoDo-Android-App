package com.bignerdranch.android.pomodo.Model

data class ToDoModel(
    var id: Int = 0,  // Change id to Int
    var task: String = "",  // This matches the property you're trying to access in task.task
    var status: Int = 0
)
