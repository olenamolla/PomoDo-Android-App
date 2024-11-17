package com.bignerdranch.android.pomodo

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.bignerdranch.android.pomodo.Model.ToDoModel

open class DatabaseHandler(context: Context?) : SQLiteOpenHelper(context, NAME, null, VERSION) {

    private var db: SQLiteDatabase? = null

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TODO_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TODO_TABLE")
        onCreate(db)
    }

    open fun openDatabase() {
        db = writableDatabase
    }

    open fun insertTask(task: ToDoModel) {
        val cv = ContentValues().apply {
            put(TASK, task.task)
            put(STATUS, task.status)
        }
        db?.insert(TODO_TABLE, null, cv)
    }

    open fun getAllTasks(): List<ToDoModel> {
        val taskList = mutableListOf<ToDoModel>()
        var cursor: Cursor? = null
        db?.beginTransaction()
        try {
            cursor = db?.query(TODO_TABLE, null, null, null, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val task = ToDoModel().apply {
                        // Use getInt to properly fetch the integer id
                        id = cursor.getInt(cursor.getColumnIndexOrThrow(ID))  // id as Int
                        task = cursor.getString(cursor.getColumnIndexOrThrow(TASK))
                        status = cursor.getInt(cursor.getColumnIndexOrThrow(STATUS))
                    }
                    taskList.add(task)
                } while (cursor.moveToNext())
            }
            db?.setTransactionSuccessful()
        } finally {
            db?.endTransaction()
            cursor?.close()
        }
        return taskList
    }

    fun updateStatus(id: Int, status: Int) {
        val cv = ContentValues().apply {
            put(STATUS, status)
        }
        db?.update(TODO_TABLE, cv, "$ID=?", arrayOf(id.toString()))
    }

    fun updateTask(id: Int, task: String) {
        val cv = ContentValues().apply {
            put(TASK, task)
        }
        db?.update(TODO_TABLE, cv, "$ID=?", arrayOf(id.toString()))
    }

    fun deleteTask(id: Int) {
        db?.delete(TODO_TABLE, "$ID=?", arrayOf(id.toString()))
    }

    companion object {
        private const val VERSION = 1
        private const val NAME = "toDoListDatabase"
        private const val TODO_TABLE = "todo"
        private const val ID = "id"
        private const val TASK = "task"
        private const val STATUS = "status"
        private const val CREATE_TODO_TABLE = "CREATE TABLE $TODO_TABLE ($ID INTEGER PRIMARY KEY AUTOINCREMENT, $TASK TEXT, $STATUS INTEGER)"
    }
}
