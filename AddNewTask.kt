package com.bignerdranch.android.pomodo


import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.bignerdranch.android.pomodo.Model.ToDoModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class AddNewTask : BottomSheetDialogFragment() {

    private lateinit var newTaskText: EditText
    private lateinit var newTaskSaveButton: Button
    private lateinit var db: DatabaseHandler

    companion object {
        const val TAG = "ActionBottomDialog"

        fun newInstance(): AddNewTask {
            return AddNewTask()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogStyle)  // Ensuring DialogStyle is referenced correctly
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.new_task, container, false)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newTaskText = view.findViewById(R.id.newTaskText)
        newTaskSaveButton = view.findViewById(R.id.newTaskButton)

        var isUpdate = false
        val bundle = arguments
        if (bundle != null) {
            isUpdate = true
            val task = bundle.getString("task")
            newTaskText.setText(task)
            task?.let {
                if (it.isNotEmpty()) {
                    newTaskSaveButton.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark)
                    )
                }
            }
        }

        db = DatabaseHandler(requireActivity())
        db.openDatabase()

        // Enable or disable the save button based on text input
        newTaskText.addTextChangedListener { text ->
            if (text.isNullOrEmpty()) {
                newTaskSaveButton.isEnabled = false
                newTaskSaveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
            } else {
                newTaskSaveButton.isEnabled = true
                newTaskSaveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
            }
        }

        newTaskSaveButton.setOnClickListener {
            val text = newTaskText.text.toString()
            if (isUpdate) {
                db.updateTask(bundle?.getInt("id") ?: 0, text)
            } else {
                val task = ToDoModel().apply {
                    this.task = text  // Correctly setting task value
                    this.status = 0   // Correctly setting the status
                }
                db.insertTask(task)
            }
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        val activity = activity
        if (activity is DialogCloseListener) {
            activity.handleDialogClose(dialog)
        }
    }
}
